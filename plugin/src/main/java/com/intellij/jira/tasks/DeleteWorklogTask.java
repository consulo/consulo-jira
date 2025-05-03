package com.intellij.jira.tasks;

import com.google.gson.JsonElement;
import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import consulo.application.progress.ProgressIndicator;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

public class DeleteWorklogTask extends AbstractBackgroundableTask {

    private String worklogId;
    private String remainingEstimate;

    public DeleteWorklogTask(@Nonnull Project project, String issueKey, String worklogId, JsonElement remainingEstimateValue) {
        super(project, "Deleting work log...", issueKey);
        this.worklogId = worklogId;
        this.remainingEstimate = remainingEstimateValue.getAsString();
    }

    @Override
    public void run(@Nonnull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.deleteIssueWorklog(issueIdOrKey, worklogId, remainingEstimate);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue Work Log has not been deleted");
        }

    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        showNotification("Jira", "Work Log deleted successfully");
    }

}
