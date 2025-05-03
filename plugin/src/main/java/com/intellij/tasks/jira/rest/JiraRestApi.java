// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.tasks.jira.rest;

import com.intellij.tasks.jira.JiraRemoteApi;
import com.intellij.tasks.jira.JiraRepository;
import com.intellij.tasks.jira.JiraVersion;
import com.intellij.tasks.jira.model.JiraIssue;
import com.intellij.tasks.jira.model.api2.JiraRestApi2;
import com.intellij.tasks.jira.model.api20alpha1.JiraRestApi20Alpha1;
import consulo.logging.Logger;
import consulo.task.CustomTaskState;
import consulo.task.Task;
import consulo.util.collection.ContainerUtil;
import consulo.util.io.CharsetToolkit;
import jakarta.annotation.Nonnull;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import jakarta.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.util.List;

;

/**
 * @author Mikhail Golubev
 */
public abstract class JiraRestApi extends JiraRemoteApi {
    private static final Logger LOG = Logger.getInstance(JiraRestApi.class);

    public static JiraRestApi fromJiraVersion(@Nonnull JiraVersion jiraVersion, @Nonnull JiraRepository repository) {
        if (jiraVersion.getMajorNumber() == 4 && jiraVersion.getMinorNumber() >= 2) {
            return new JiraRestApi20Alpha1(repository);
        }
        else if (jiraVersion.getMajorNumber() >= 5) {
            return new JiraRestApi2(repository);
        }
        else {
            LOG.warn("JIRA below 4.2.0 doesn't support REST API (" + jiraVersion + " used)");
            return null;
        }
    }

    public static JiraRestApi fromJiraVersion(@Nonnull String version, @Nonnull JiraRepository repository) {
        return fromJiraVersion(new JiraVersion(version), repository);
    }

    protected JiraRestApi(@Nonnull JiraRepository repository) {
        super(repository);
    }

    @Override
    public final @Nonnull List<Task> findTasks(@Nonnull String jql, int max) throws Exception {
        GetMethod method = getMultipleIssuesSearchMethod(jql, max);
        String response = myRepository.executeMethod(method);
        List<JiraIssue> issues = parseIssues(response);
        return ContainerUtil.map(issues, issue -> new JiraRestTask(issue, myRepository));
    }

    @Override
    public final @Nullable JiraRestTask findTask(@Nonnull String key) throws Exception {
        GetMethod method = getSingleIssueSearchMethod(key);
        try {
            return new JiraRestTask(parseIssue(myRepository.executeMethod(method)), myRepository);
        }
        catch (Exception ignored) {
            // should be logged already
            return null;
        }
    }

    protected @Nonnull GetMethod getSingleIssueSearchMethod(String key) {
        return new GetMethod(myRepository.getRestUrl("issue", key));
    }

    protected @Nonnull GetMethod getMultipleIssuesSearchMethod(String jql, int max) {
        GetMethod method = new GetMethod(myRepository.getRestUrl("search"));
        method.setQueryString(new NameValuePair[]{
            new NameValuePair("jql", jql),
            new NameValuePair("maxResults", String.valueOf(max))
        });
        return method;
    }

    protected abstract @Nonnull List<JiraIssue> parseIssues(String response);

    protected abstract @Nullable JiraIssue parseIssue(String response);

    @Override
    public void setTaskState(@Nonnull Task task, @Nonnull CustomTaskState state) throws Exception {
        String requestBody = getRequestForStateTransition(state);
        LOG.debug(String.format("Transition: %s -> %s, request: %s", task.getState(), state, requestBody));
        PostMethod method = new PostMethod(myRepository.getRestUrl("issue", task.getId(), "transitions"));
        method.setRequestEntity(createJsonEntity(requestBody));
        myRepository.executeMethod(method);
    }

    protected abstract @Nullable String getRequestForStateTransition(@Nonnull CustomTaskState state);

    protected static RequestEntity createJsonEntity(String requestBody) {
        try {
            return new StringRequestEntity(requestBody, "application/json", CharsetToolkit.UTF8);
        }
        catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 encoding is not supported");
        }
    }
}
