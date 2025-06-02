// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.tasks.jira;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.tasks.jira.rest.JiraRestApi;
import com.intellij.tasks.jira.soap.JiraLegacyApi;
import consulo.logging.Logger;
import consulo.task.CustomTaskState;
import consulo.task.LocalTask;
import consulo.task.Task;
import consulo.task.TaskBundle;
import consulo.task.util.gson.TaskGsonUtil;
import consulo.util.collection.Lists;
import consulo.util.io.StreamUtil;
import consulo.util.lang.Comparing;
import consulo.util.lang.StringUtil;
import consulo.util.xml.serializer.annotation.Tag;
import jakarta.annotation.Nonnull;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.HttpAuthenticator;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import jakarta.annotation.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Dmitry Avdeev
 */
@SuppressWarnings("UseOfObsoleteCollectionType")
@Tag("JIRA")
public final class JiraRepository extends BaseRepositoryImpl {

    public static final Gson GSON = TaskGsonUtil.createDefaultBuilder().create();
    private static final Logger LOG = Logger.getInstance(JiraRepository.class);
    public static final String REST_API_PATH = "/rest/api/latest";

    private static final boolean LEGACY_API_ONLY = Boolean.getBoolean("tasks.jira.legacy.api.only");
    private static final boolean BASIC_AUTH_ONLY = Boolean.getBoolean("tasks.jira.basic.auth.only");
    private static final boolean REDISCOVER_API = Boolean.getBoolean("tasks.jira.rediscover.api");

    public static final Pattern JIRA_ID_PATTERN = Pattern.compile("\\p{javaUpperCase}+-\\d+");
    public static final String AUTH_COOKIE_NAME = "JSESSIONID";

    /**
     * Default JQL query
     */
    private String mySearchQuery = TaskBundle.message("jira.default.query");

    private JiraRemoteApi myApiVersion;
    private String myJiraVersion;
    private boolean myInCloud = false;
    private boolean myUseBearerTokenAuthentication;

    /**
     * Serialization constructor
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public JiraRepository() {
        setUseHttpAuthentication(true);
    }

    public JiraRepository(JiraRepositoryType type) {
        super(type);
        // Use Basic authentication at the beginning of new session and disable then if needed
        setUseHttpAuthentication(true);
    }

    private JiraRepository(JiraRepository other) {
        super(other);
        mySearchQuery = other.mySearchQuery;
        myJiraVersion = other.myJiraVersion;
        myInCloud = other.myInCloud;
        myUseBearerTokenAuthentication = other.myUseBearerTokenAuthentication;
        if (other.myApiVersion != null) {
            myApiVersion = other.myApiVersion.getType().createApi(this);
        }
    }

    @Override
    @SuppressWarnings("EqualsHashCode")
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof JiraRepository repository)) {
            return false;
        }

        if (!Objects.equals(mySearchQuery, repository.getSearchQuery())) {
            return false;
        }
        if (!Objects.equals(myJiraVersion, repository.getJiraVersion())) {
            return false;
        }
        if (!Comparing.equal(myInCloud, repository.isInCloud())) {
            return false;
        }
        if (!Comparing.equal(myUseBearerTokenAuthentication, repository.isUseBearerTokenAuthentication())) {
            return false;
        }
        return true;
    }


    @Override
    public @Nonnull JiraRepository clone() {
        return new JiraRepository(this);
    }

    @Override
    public Task[] getIssues(@Nullable String query, int max, long since) throws Exception {
        ensureApiVersionDiscovered();
        String resultQuery = StringUtil.notNullize(query);
        if (isJqlSupported()) {
            if (StringUtil.isNotEmpty(mySearchQuery) && StringUtil.isNotEmpty(query)) {
                resultQuery = String.format("summary ~ '%s' and ", query) + mySearchQuery;
            }
            else if (StringUtil.isNotEmpty(query)) {
                resultQuery = String.format("summary ~ '%s'", query);
            }
            else {
                resultQuery = mySearchQuery;
            }
        }
        List<Task> tasksFound = myApiVersion.findTasks(resultQuery, max);
        // JQL matching doesn't allow to do something like "summary ~ query or key = query"
        // and it will return error immediately. So we have to search in two steps to provide
        // behavior consistent with e.g. YouTrack.
        // looks like issue ID
        if (query != null && JIRA_ID_PATTERN.matcher(query.trim()).matches()) {
            Task task = findTask(query);
            if (task != null) {
                tasksFound = Lists.append(tasksFound, task);
            }
        }
        return tasksFound.toArray(Task.EMPTY_ARRAY);
    }

    @Override
    @Nullable
    public Task findTask(@Nonnull String id) throws Exception {
        ensureApiVersionDiscovered();
        return myApiVersion.findTask(id);
    }

    @Override
    public void updateTimeSpent(@Nonnull LocalTask task, @Nonnull String timeSpent, @Nonnull String comment) throws Exception {
        myApiVersion.updateTimeSpend(task, timeSpent, comment);
    }

    @Override
    @Nullable
    public CancellableConnection createCancellableConnection() {
        clearCookies();
        // TODO cancellable connection for XML_RPC?
        return new CancellableConnection() {
            @Override
            protected void doTest() throws Exception {
                ensureApiVersionDiscovered();
                myApiVersion.findTasks(mySearchQuery, 1);
            }

            @Override
            public void cancel() {
                // do nothing for now
            }
        };
    }

    public @Nonnull JiraRemoteApi discoverApiVersion() throws Exception {
        if (LEGACY_API_ONLY) {
            LOG.info("Intentionally using only legacy JIRA API");
            return createLegacyApi();
        }

        String responseBody;
        GetMethod method = new GetMethod(getRestUrl("serverInfo"));
        try {
            responseBody = executeMethod(method);
        }
        catch (Exception e) {
            // probably JIRA version prior 4.2
            // It's not safe to call HttpMethod.getStatusCode() directly, because it will throw NPE
            // if response was not received (connection lost etc.) and hasBeenUsed()/isRequestSent() are
            // not the way to check it safely.
            StatusLine status = method.getStatusLine();
            if (status != null && status.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                return createLegacyApi();
            }
            else {
                throw e;
            }
        }
        JsonObject serverInfo = GSON.fromJson(responseBody, JsonObject.class);
        // when JIRA 4.x support will be dropped 'versionNumber' array in response
        // may be used instead version string parsing
        myJiraVersion = serverInfo.get("version").getAsString();
        myInCloud = isHostedInCloud(serverInfo);
        LOG.info("JIRA version (from serverInfo): " + getPresentableVersion());
        if (isInCloud()) {
            LOG.info("Connecting to JIRA Cloud. Cookie authentication is enabled unless 'tasks.jira.basic.auth.only' VM flag is used.");
        }
        JiraRestApi restApi = JiraRestApi.fromJiraVersion(myJiraVersion, this);
        if (restApi == null) {
            throw new Exception(TaskBundle.message("jira.failure.no.REST"));
        }
        return restApi;
    }

    private static boolean isHostedInCloud(@Nonnull JsonObject serverInfo) {
        final JsonElement deploymentType = serverInfo.get("deploymentType");
        if (deploymentType != null) {
            return deploymentType.getAsString().equals("Cloud");
        }
        // Legacy heuristics
        final boolean atlassianSubDomain = isAtlassianNetSubDomain(serverInfo.get("baseUrl").getAsString());
        if (atlassianSubDomain) {
            return true;
        }
        // JIRA OnDemand versions contained "OD" abbreviation
        return serverInfo.get("version").getAsString().contains("OD");
    }

    private static boolean isAtlassianNetSubDomain(@Nonnull String url) {
        return hostEndsWith(url, ".atlassian.net");
    }

    private static boolean hostEndsWith(@Nonnull String url, @Nonnull String suffix) {
        try {
            final URL parsed = new URL(url);
            return parsed.getHost().endsWith(suffix);
        }
        catch (MalformedURLException ignored) {
        }
        return false;
    }

    private JiraLegacyApi createLegacyApi() {
        try {
            XmlRpcClient client = new XmlRpcClient();
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(getUrl()));
            client.setConfig(config);
            XmlRpcCommonsTransportFactory transportFactory = new XmlRpcCommonsTransportFactory(client);
            transportFactory.setHttpClient(getHttpClient());
            client.setTransportFactory(transportFactory);

            List<String> parameters = new ArrayList<>(Collections.singletonList(""));
            @SuppressWarnings("unchecked") Hashtable<String, Object> response = (Hashtable<String, Object>) client.execute("jira1.getServerInfo", parameters);
            if (response != null) {
                myJiraVersion = (String) response.get("version");
            }
        }
        catch (Exception e) {
            LOG.error("Cannot find out JIRA version via XML-RPC", e);
        }
        return new JiraLegacyApi(this);
    }

    private void ensureApiVersionDiscovered() throws Exception {
        if (myApiVersion == null || LEGACY_API_ONLY || REDISCOVER_API) {
            myApiVersion = discoverApiVersion();
        }
    }

    @Nonnull
    public String executeMethod(@Nonnull HttpMethod method) throws Exception {
        LOG.debug("URI: " + method.getURI());

        HttpClient client = getHttpClient();
        // Fix for https://jetbrains.zendesk.com/agent/#/tickets/24566
        // See https://confluence.atlassian.com/display/ONDEMANDKB/Getting+randomly+logged+out+of+OnDemand for details
        // IDEA-128824, IDEA-128706 Use cookie authentication only for JIRA on-Demand
        // TODO Make JiraVersion more suitable for such checks
        if (BASIC_AUTH_ONLY) {
            // to override persisted settings
            setUseHttpAuthentication(true);
        }
        else if (!isInCloud()) {
            if (isUseBearerTokenAuthentication()) {
                setUseHttpAuthentication(false);
                method.addRequestHeader(new Header(HttpAuthenticator.WWW_AUTH_RESP, "Bearer " + getPassword()));
            }
            else {
                setUseHttpAuthentication(true);
            }
        }
        else {
            boolean enableBasicAuthentication = !(isRestApiSupported() && containsCookie(client, AUTH_COOKIE_NAME));
            if (enableBasicAuthentication != isUseHttpAuthentication()) {
                LOG.info("Basic authentication for subsequent requests was " + (enableBasicAuthentication ? "enabled" : "disabled"));
            }
            setUseHttpAuthentication(enableBasicAuthentication);
        }

        int statusCode = client.executeMethod(method);
        LOG.debug("Status code: " + statusCode);
        // may be null if 204 No Content received
        InputStream stream = method.getResponseBodyAsStream();
        String entityContent = "";
        if (stream != null) {
            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                entityContent = StreamUtil.readTextFrom(reader);
            }
        }
        //TaskUtil.prettyFormatJsonToLog(LOG, entityContent);
        // besides SC_OK, can also be SC_NO_CONTENT in issue transition requests
        // see: JiraRestApi#setTaskStatus
        //if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NO_CONTENT) {
        if (statusCode >= 200 && statusCode < 300) {
            return entityContent;
        }
        clearCookies();
        if (method.getResponseHeader("Content-Type") != null) {
            Header header = method.getResponseHeader("Content-Type");
            if (header.getValue().startsWith("application/json")) {
                JsonObject object = GSON.fromJson(entityContent, JsonObject.class);
                if (object.has("errorMessages")) {
                    String reason = StringUtil.join(object.getAsJsonArray("errorMessages"), " ");
                    // If anonymous access is enabled on server, it might reply only with a cryptic 400 error about inaccessible issue fields,
                    // e.g. "Field 'assignee' does not exist or this field cannot be viewed by anonymous users."
                    // Unfortunately, there is no better way to indicate such errors other than by matching by the error message itself.
                    LOG.warn(reason);
                    if (statusCode == HttpStatus.SC_BAD_REQUEST && reason.contains("cannot be viewed by anonymous users")) {
                        // Oddly enough, in case of JIRA Cloud issues are access anonymously only if API Token is correct, but email is wrong.
                        throw new Exception(isInCloud() ? TaskBundle.message("jira.failure.email.address") : TaskBundle.message("failure.login"));
                    }
                    // something meaningful to user, e.g. invalid field name in JQL query
                    throw new Exception(TaskBundle.message("failure.server.message", reason));
                }
            }
        }
        if (method.getResponseHeader("X-Authentication-Denied-Reason") != null) {
            Header header = method.getResponseHeader("X-Authentication-Denied-Reason");
            // only in JIRA >= 5.x.x
            if (header.getValue().startsWith("CAPTCHA_CHALLENGE")) {
                throw new Exception(TaskBundle.message("jira.failure.captcha"));
            }
        }
        if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            throw new Exception(TaskBundle.message("failure.login"));
        }
        String statusText = HttpStatus.getStatusText(method.getStatusCode());
        throw new Exception(TaskBundle.message("failure.http.error", statusCode, statusText));
    }

    public boolean isInCloud() {
        return myInCloud;
    }

    public void setInCloud(boolean inCloud) {
        myInCloud = inCloud;
    }

    public boolean isUseBearerTokenAuthentication() {
        return myUseBearerTokenAuthentication;
    }

    public void setUseBearerTokenAuthentication(boolean useBearerTokenAuthentication) {
        if (useBearerTokenAuthentication != isUseBearerTokenAuthentication()) {
            myUseBearerTokenAuthentication = useBearerTokenAuthentication;
            reconfigureClient();
        }
    }

    @Nonnull
    String getPresentableVersion() {
        return StringUtil.notNullize(myJiraVersion, "unknown") + (myInCloud ? " (Cloud)" : "");
    }

    private static boolean containsCookie(@Nonnull HttpClient client, @Nonnull String cookieName) {
        for (Cookie cookie : client.getState().getCookies()) {
            if (cookie.getName().equals(cookieName) && !cookie.isExpired()) {
                return true;
            }
        }
        return false;
    }

    private void clearCookies() {
        getHttpClient().getState().clearCookies();
    }

    // Made public for SOAP API compatibility
    @Override
    public HttpClient getHttpClient() {
        return super.getHttpClient();
    }

    @Override
    protected void configureHttpClient(HttpClient client) {
        super.configureHttpClient(client);
        if (isUseBearerTokenAuthentication()) {
            client.getParams().setAuthenticationPreemptive(true);
            client.getState().clearCredentials();
        }
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    }

    @Override
    protected int getFeatures() {
        int features = super.getFeatures();
        if (isRestApiSupported()) {
            return features | TIME_MANAGEMENT | STATE_UPDATING;
        }
        else {
            return features & ~NATIVE_SEARCH & ~STATE_UPDATING & ~TIME_MANAGEMENT;
        }
    }

    private boolean isRestApiSupported() {
        return myApiVersion == null || myApiVersion.getType() != JiraRemoteApi.ApiType.LEGACY;
    }

    public boolean isJqlSupported() {
        return isRestApiSupported();
    }

    public String getSearchQuery() {
        return mySearchQuery;
    }

    @Override
    public void setTaskState(@Nonnull Task task, @Nonnull CustomTaskState state) throws Exception {
        myApiVersion.setTaskState(task, state);
    }

    @Override
    public @Nonnull Set<CustomTaskState> getAvailableTaskStates(@Nonnull Task task) throws Exception {
        return myApiVersion.getAvailableTaskStates(task);
    }

    public void setSearchQuery(String searchQuery) {
        mySearchQuery = searchQuery;
    }

    @Override
    public void setUrl(String url) {
        // Compare only normalized URLs
        final String oldUrl = getUrl();
        super.setUrl(url);
        // reset remote API version, only if server URL was changed
        if (!getUrl().equals(oldUrl)) {
            myApiVersion = null;
            myInCloud = isAtlassianNetSubDomain(getUrl());
        }
    }

    /**
     * Used to preserve discovered API version for the next initialization.
     */
    @SuppressWarnings("UnusedDeclaration")
    public @Nullable JiraRemoteApi.ApiType getApiType() {
        return myApiVersion == null ? null : myApiVersion.getType();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setApiType(@Nullable JiraRemoteApi.ApiType type) {
        if (type != null) {
            myApiVersion = type.createApi(this);
        }
    }

    public @Nullable String getJiraVersion() {
        return myJiraVersion;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setJiraVersion(@Nullable String jiraVersion) {
        myJiraVersion = jiraVersion;
    }

    public String getRestUrl(String... parts) {
        return getUrl() + REST_API_PATH + "/" + StringUtil.join(parts, "/");
    }
}