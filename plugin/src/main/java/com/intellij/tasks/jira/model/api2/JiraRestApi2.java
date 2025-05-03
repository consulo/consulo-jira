// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.tasks.jira.model.api2;

import com.google.gson.reflect.TypeToken;
import com.intellij.tasks.jira.JiraRepository;
import com.intellij.tasks.jira.model.JiraIssue;
import com.intellij.tasks.jira.model.JiraResponseWrapper;
import com.intellij.tasks.jira.rest.JiraRestApi;
import com.intellij.tasks.jira.rest.api2.model.JiraTransitionsWrapperApi2;
import consulo.logging.Logger;
import consulo.task.CustomTaskState;
import consulo.task.LocalTask;
import consulo.task.Task;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nonnull;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import jakarta.annotation.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This REST API version is used in JIRA 5.1.8 and above (including JIRA 6.x.x).
 *
 * @author Mikhail Golubev
 */
public class JiraRestApi2 extends JiraRestApi {
    private static final Logger LOG = Logger.getInstance(JiraIssueApi2.class);
    private static final Type ISSUES_WRAPPER_TYPE = new TypeToken<JiraResponseWrapper.Issues<JiraIssueApi2>>() { /* empty */
    }.getType();

    public JiraRestApi2(JiraRepository repository) {
        super(repository);
    }

    @Override
    protected @Nonnull GetMethod getMultipleIssuesSearchMethod(String jql, int max) {
        GetMethod method = super.getMultipleIssuesSearchMethod(jql, max);
        method.setQueryString(method.getQueryString() + "&fields=" + JiraIssueApi2.REQUIRED_RESPONSE_FIELDS);
        return method;
    }

    @Override
    protected @Nonnull List<JiraIssue> parseIssues(String response) {
        JiraResponseWrapper.Issues<JiraIssueApi2> wrapper = JiraRepository.GSON.fromJson(response, ISSUES_WRAPPER_TYPE);
        return new ArrayList<>(wrapper.getIssues());
    }

    @Override
    protected @Nonnull GetMethod getSingleIssueSearchMethod(String key) {
        final GetMethod method = super.getSingleIssueSearchMethod(key);
        final String oldParams = method.getQueryString() == null ? "" : method.getQueryString();
        method.setQueryString(oldParams + "&fields=" + JiraIssueApi2.REQUIRED_RESPONSE_FIELDS);
        return method;
    }

    @Override
    protected @Nullable JiraIssue parseIssue(String response) {
        return JiraRepository.GSON.fromJson(response, JiraIssueApi2.class);
    }

    @Override
    protected @Nonnull String getRequestForStateTransition(@Nonnull CustomTaskState state) {
        assert StringUtil.isNotEmpty(state.getId());
        final String stateId = state.getId();
        final int index = stateId.indexOf(':');
        if (index >= 0) {
            return "{" +
                "  \"transition\": {\"id\": \"" + stateId.substring(0, index) + "\"}, " +
                "  \"fields\": {\"resolution\": {\"name\": \"" + stateId.substring(index + 1) + "\"}}" +
                "}";
        }
        else {
            return "{\"transition\": {\"id\": \"" + stateId + "\"}}";
        }
    }

    @Override
    public @Nonnull Set<CustomTaskState> getAvailableTaskStates(@Nonnull Task task) throws Exception {
        final GetMethod method = new GetMethod(myRepository.getRestUrl("issue", task.getId(), "transitions"));
        method.setQueryString("expand=transitions.fields");
        final String response = myRepository.executeMethod(method);
        final JiraTransitionsWrapperApi2 wrapper = JiraRepository.GSON.fromJson(response, JiraTransitionsWrapperApi2.class);
        return wrapper.getTransitions();
    }

    @Override
    public void updateTimeSpend(@Nonnull LocalTask task, @Nonnull String timeSpent, String comment) throws Exception {
        LOG.debug(String.format("Time spend: %s, comment: %s", timeSpent, comment));
        PostMethod method = new PostMethod(myRepository.getRestUrl("issue", task.getId(), "worklog"));
        String request;
        if (StringUtil.isEmpty(comment)) {
            request = String.format("{\"timeSpent\" : \"" + timeSpent + "\"}", timeSpent);
        }
        else {
            request = String.format("{\"timeSpent\": \"%s\", \"comment\": \"%s\"}", timeSpent, StringUtil.escapeQuotes(comment));
        }
        method.setRequestEntity(createJsonEntity(request));
        myRepository.executeMethod(method);
    }

    @Override
    public @Nonnull ApiType getType() {
        return ApiType.REST_2_0;
    }
}
