package com.intellij.jira.tasks;

import com.google.gson.JsonElement;
import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.helper.TransitionFieldHelper;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import consulo.application.progress.ProgressIndicator;
import consulo.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddWorklogTask extends AbstractBackgroundableTask {

    private List<TransitionFieldHelper.FieldEditorInfo> worklogFields;
    private String remainingEstimate;

    public AddWorklogTask(@NotNull Project project, String issueKey, List<TransitionFieldHelper.FieldEditorInfo> worklogFields, JsonElement remainingEstimateValue) {
        super(project, "Adding Work Log", issueKey);
        this.worklogFields = worklogFields;
        this.remainingEstimate = remainingEstimateValue.getAsString();
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.addIssueWorklog(issueIdOrKey, worklogFields, remainingEstimate);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue Work Log has not been added");
        }

    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        showNotification("Jira", "Work Log added successfully");
    }

}
