package com.intellij.jira.rest.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intellij.jira.server.JiraServer;
import com.intellij.tasks.jira.JiraRepository;
import consulo.logging.Logger;
import consulo.task.TaskBundle;
import consulo.task.TaskSettings;
import consulo.task.util.gson.TaskGsonUtil;
import consulo.util.io.StreamUtil;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.intellij.jira.util.JiraLabelUtil.EMPTY_TEXT;

public class JiraRestTemplate {

    private final JiraServer myJiraServer;
    private String myUrl = "";

    protected String myUsername = "";
    protected String myPassword = "";
    protected boolean myUseHttpAuthentication;

    private final HttpClient myClient;


    public static final Gson GSON = TaskGsonUtil.createDefaultBuilder().create();
    private final static Logger LOG = Logger.getInstance(JiraRepository.class);
    public static final String REST_API_PATH = "/rest/api/latest";

    private static final boolean BASIC_AUTH_ONLY = Boolean.getBoolean("tasks.jira.basic.auth.only");

    public static final String AUTH_COOKIE_NAME = "JSESSIONID";

    /**
     * Default JQL query
     */
    private String mySearchQuery = TaskBundle.message("jira.default.query");
    private String myJiraVersion;
    private boolean myInCloud = false;

    /**
     * Serialization constructor
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public JiraRestTemplate(JiraServer jiraServer) {
        myJiraServer = jiraServer;
        myClient = createClient();
        setUseHttpAuthentication(true);
        setUrl(jiraServer.getUrl());
        setUsername(StringUtil.defaultIfEmpty(jiraServer.getUsername(), EMPTY_TEXT));
        setPassword(StringUtil.defaultIfEmpty(jiraServer.getPassword(), EMPTY_TEXT));
    }


    private HttpClient createClient() {
        HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
        configureHttpClient(client);
        return client;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (!(o instanceof JiraRepository repository)) return false;

        if (!Objects.equals(mySearchQuery, repository.getSearchQuery())) return false;
        //TODO if (!Objects.equals(myJiraVersion, repository.getJiraVersion())) return false;
        //TODO if (!Comparing.equal(myInCloud, repository.isInCloud())) return false;
        return true;
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


    @Nonnull
    public String executeMethod(@Nonnull HttpMethod method) throws Exception {
        LOG.debug("URI: " + method.getURI());

        HttpClient client = getHttpClient();
        if (BASIC_AUTH_ONLY || !isInCloud()) {
            // to override persisted settings
            setUseHttpAuthentication(true);
        }
        else {
            boolean enableBasicAuthentication = !(containsCookie(client, AUTH_COOKIE_NAME));
            if (enableBasicAuthentication != isUseHttpAuthentication()) {
                LOG.info("Basic authentication for subsequent requests was " + (enableBasicAuthentication ? "enabled" : "disabled"));
            }
            setUseHttpAuthentication(enableBasicAuthentication);
        }

        if (myJiraServer.hasAccessTokenAuth()) {
            method.addRequestHeader("Authorization", "Bearer " + getPassword());
        }

        int statusCode = client.executeMethod(method);
        LOG.debug("Status code: " + statusCode);
        // may be null if 204 No Content received
        InputStream stream = method.getResponseBodyAsStream();
        String entityContent = "";
        if (stream != null) {
            entityContent = StreamUtil.readText(stream, StandardCharsets.UTF_8);
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
                    if (statusCode ==  HttpStatus.SC_BAD_REQUEST && reason.contains("cannot be viewed by anonymous users")) {
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

    public HttpClient getHttpClient() {
        return myClient;
    }


    public String getSearchQuery() {
        return mySearchQuery;
    }



    @SuppressWarnings("UnusedDeclaration")


    @Nullable
    public String getJiraVersion() {
        return myJiraVersion;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setJiraVersion(@Nullable String jiraVersion) {
        myJiraVersion = jiraVersion;
    }

    public String getRestUrl(String... parts) {
        return getUrl() + REST_API_PATH + "/" + StringUtil.join(parts, "/");
    }

    public void setUseHttpAuthentication(boolean useHttpAuthentication) {
        if (useHttpAuthentication != isUseHttpAuthentication()) {
            myUseHttpAuthentication = useHttpAuthentication;
            reconfigureClient();
        }
    }

    public void setPassword(String password) {
        if (!Objects.equals(password, getPassword())) {
            myPassword = password;
            reconfigureClient();
        }
    }

    public void setUsername(String username) {
        if (!username.equals(getUsername())) {
            myUsername = username;
            reconfigureClient();
        }
    }

    public final void reconfigureClient() {
        synchronized (myClient) {
            configureHttpClient(myClient);
        }
    }

    protected void configureHttpClient(HttpClient client) {
        client.getParams().setConnectionManagerTimeout(3000);
        client.getParams().setSoTimeout(TaskSettings.getInstance().CONNECTION_TIMEOUT);

        if (isUseHttpAuthentication()) {
            client.getParams().setCredentialCharset("UTF-8");
            client.getParams().setAuthenticationPreemptive(true);
            client.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(getUsername(), getPassword()));
        }
        else {
            client.getState().clearCredentials();
            client.getParams().setAuthenticationPreemptive(false);
        }
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    }

    public boolean isUseHttpAuthentication() {
        return myUseHttpAuthentication;
    }

    public String getUsername() {
        return myUsername;
    }


    public String getPassword() {
        return myPassword;
    }

    public String getUrl() {
        return trimTrailingSlashes(myUrl);
    }

    public void setUrl(String url) {
        final String oldUrl = getUrl();
        myUrl = addSchemeIfNoneSpecified(trimTrailingSlashes(url));
        // reset remote API version, only if server URL was changed
        if (!getUrl().equals(oldUrl)) {
            myInCloud = isAtlassianNetSubDomain(getUrl());
        }


    }

    private String addSchemeIfNoneSpecified(@Nullable String url) {
        if (StringUtil.isNotEmpty(url)) {
            try {
                final String scheme = new URI(url).getScheme();
                // For URL like "foo.bar:8080" host name will be parsed as scheme
                if (scheme == null) {
                    url = getDefaultScheme() + "://" + url;
                }
            }
            catch (URISyntaxException ignored) {
            }
        }
        return url;
    }

    @Nonnull
    protected String getDefaultScheme() {
        return "http";
    }

    private static String trimTrailingSlashes(String url) {
        if (url == null) return "";
        for (int i = url.length() - 1; i >= 0; i--) {
            if (url.charAt(i) != '/') {
                return url.substring(0, i + 1);
            }
        }
        return "";
    }
}
