// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.tasks.jira.soap;

import com.intellij.tasks.jira.JiraRemoteApi;
import com.intellij.tasks.jira.JiraRepository;
import consulo.logging.Logger;
import consulo.task.CustomTaskState;
import consulo.task.LocalTask;
import consulo.task.Task;
import consulo.task.TaskBundle;
import consulo.task.util.TaskUtil;
import consulo.util.collection.ContainerUtil;
import jakarta.annotation.Nonnull;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jetbrains.annotations.NonNls;
import jakarta.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Legacy integration restored due to IDEA-120595.
 *
 * @author Mikhail Golubev
 */
public final class JiraLegacyApi extends JiraRemoteApi {
    private static final Logger LOG = Logger.getInstance(JiraLegacyApi.class);

    private static final @NonNls String RSS_SEARCH_PATH = "/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml";
    private static final String RSS_ISSUE_PATH = "/si/jira.issueviews:issue-xml/";

    public JiraLegacyApi(@Nonnull JiraRepository repository) {
        super(repository);
    }

    @Override
    public @Nonnull List<Task> findTasks(@Nonnull String query, int max) throws Exception {

        // Unfortunately, both SOAP and XML-RPC interfaces of JIRA don't allow fetching *all* tasks from server, but
        // only filtered by some search term (see http://stackoverflow.com/questions/764282/how-can-jira-soap-api-not-have-this-method).
        // JQL was added in SOAP only since JIRA 4.0 (see method JiraSoapService#getIssuesFromJqlSearch() at
        // https://docs.atlassian.com/software/jira/docs/api/rpc-jira-plugin/latest/index.html?com/atlassian/jira/rpc/soap/JiraSoapService.html)
        // So due to this limitation and the need to support these old versions of bug tracker (3.0, 4.2) we need the following ugly and hacky
        // solution with extracting issues from RSS feed.

        GetMethod method = new GetMethod(myRepository.getUrl() + RSS_SEARCH_PATH);
        method.setQueryString(new NameValuePair[]{
            new NameValuePair("tempMax", String.valueOf(max)),
            new NameValuePair("assignee", TaskUtil.encodeUrl(myRepository.getUsername())),
            new NameValuePair("reset", "true"),
            new NameValuePair("sorter/field", "updated"),
            new NameValuePair("sorter/order", "DESC"),
            new NameValuePair("pager/start", "0")
        });
        return processRSS(method);
    }

    private List<Task> processRSS(@Nonnull GetMethod method) throws Exception {
        // Basic authorization should be enough
        int code = myRepository.getHttpClient().executeMethod(method);
        if (code != HttpStatus.SC_OK) {
            throw new Exception(TaskBundle.message("failure.http.error", code, method.getStatusText()));
        }
        Element root = new SAXBuilder(false).build(method.getResponseBodyAsStream()).getRootElement();
        Element channel = root.getChild("channel");
        if (channel != null) {
            List<Element> children = channel.getChildren("item");
            LOG.debug("Total issues in JIRA RSS feed: " + children.size());
            return ContainerUtil.map(children, element -> new JiraSoapTask(element, myRepository));
        }
        else {
            LOG.warn("JIRA channel not found");
        }
        return List.of();
    }

    @Override
    public @Nullable Task findTask(@Nonnull String key) throws Exception {
        try {
            List<Task> tasks = processRSS(new GetMethod(myRepository.getUrl() + RSS_ISSUE_PATH + key + '/' + key + ".xml"));
            return tasks.isEmpty() ? null : tasks.get(0);
        }
        catch (Exception e) {
            LOG.warn("Cannot get issue " + key + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    public @Nonnull ApiType getType() {
        return ApiType.LEGACY;
    }

    @Override
    public @Nonnull Set<CustomTaskState> getAvailableTaskStates(@Nonnull Task task) throws Exception {
        return Collections.emptySet();
    }

    @Override
    public void setTaskState(@Nonnull Task task, @Nonnull CustomTaskState state) throws Exception {
        throw new Exception(TaskBundle.message("jira.failure.no.state.update"));
    }

    @Override
    public void updateTimeSpend(@Nonnull LocalTask task, @Nonnull String timeSpent, String comment) throws Exception {
        throw new Exception(TaskBundle.message("jira.failure.no.time.spent"));
    }
}
