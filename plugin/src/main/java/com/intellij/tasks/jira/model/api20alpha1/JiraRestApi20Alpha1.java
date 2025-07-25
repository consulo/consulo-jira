// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.tasks.jira.model.api20alpha1;

import com.google.gson.reflect.TypeToken;
import com.intellij.tasks.jira.JiraRepository;
import com.intellij.tasks.jira.model.JiraIssue;
import com.intellij.tasks.jira.model.JiraResponseWrapper;
import com.intellij.tasks.jira.rest.JiraRestApi;
import com.intellij.tasks.jira.rest.JiraRestTask;
import consulo.logging.Logger;
import consulo.task.CustomTaskState;
import consulo.task.LocalTask;
import consulo.task.Task;
import consulo.task.TaskBundle;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This REST API is used in JIRA versions from 4.2 to 4.4.
 *
 * @author Mikhail Golubev
 */
public class JiraRestApi20Alpha1 extends JiraRestApi {
    private static final Logger LOG = Logger.getInstance(JiraRestApi20Alpha1.class);
    private static final Type ISSUES_WRAPPER_TYPE = new TypeToken<JiraResponseWrapper.Issues<JiraIssueApi20Alpha1>>() {/* empty */
    }.getType();

    public JiraRestApi20Alpha1(JiraRepository repository) {
        super(repository);
    }

    @Override
    public @Nonnull Set<CustomTaskState> getAvailableTaskStates(@Nonnull Task task) throws Exception {
        // REST API of JIRA 4.x for retrieving possible transitions is very limited: we can't fetch possible resolutions and
        // names of transition destinations. So we have no other options than to hardcode them.
        final HashSet<CustomTaskState> result = new HashSet<>();
        result.add(new CustomTaskState("4", "In Progress"));
        result.add(new CustomTaskState("5", "Resolved (Fixed)"));
        result.add(new CustomTaskState("3", "Reopened"));
        return result;
    }

    @Override
    protected JiraIssue parseIssue(String response) {
        return JiraRepository.GSON.fromJson(response, JiraIssueApi20Alpha1.class);
    }

    @Override
    protected @Nonnull List<JiraIssue> parseIssues(String response) {
        JiraResponseWrapper.Issues<JiraIssueApi20Alpha1> wrapper = JiraRepository.GSON.fromJson(response, ISSUES_WRAPPER_TYPE);
        List<JiraIssueApi20Alpha1> incompleteIssues = wrapper.getIssues();
        List<JiraIssue> updatedIssues = new ArrayList<>();
        for (JiraIssueApi20Alpha1 issue : incompleteIssues) {
            try {
                JiraRestTask task = findTask(issue.getKey());
                if (task != null) {
                    updatedIssues.add(task.getJiraIssue());
                }
            }
            catch (Exception e) {
                LOG.warn("Can't fetch detailed info about issue: " + issue.getKey());
            }
        }
        return updatedIssues;
    }

    @Override
    protected @Nullable String getRequestForStateTransition(@Nonnull CustomTaskState state) {
        try {
            switch (Integer.parseInt(state.getId())) {
                case 4 -> { // In Progress
                    return "{\"transition\": \"4\"}";
                }
                case 5 -> { // Resolved (2 for "Closed")
                    return "{\"transition\": \"5\", \"resolution\": \"Fixed\"}";
                }
                case 3 -> { // Reopened
                    return "{\"transition\": \"3\"}";
                }
            }
        }
        catch (NumberFormatException ignored) {
        }
        LOG.error("Unknown ID of predefined issue state: " + state.getId());
        return null;
    }


    @Override
    public void updateTimeSpend(@Nonnull LocalTask task, @Nonnull String timeSpent, String comment) throws Exception {
        throw new Exception(TaskBundle.message("jira.failure.no.time.spent"));
    }

    @Override
    public @Nonnull ApiType getType() {
        return ApiType.REST_2_0_ALPHA;
    }
}
