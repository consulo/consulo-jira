package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import consulo.application.progress.ProgressIndicator;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

public class AssignUserTask extends AbstractBackgroundableTask {

    private String accountId;
    private String username;

    public AssignUserTask(@Nonnull Project project, String accountId, String username, String issueKey) {
        super(project, "Assigning User to Issue...", issueKey);
        this.accountId = accountId;
        this.username = username;
    }

    @Override
    public void run(@Nonnull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();
        Result result = jiraRestApi.assignUserToIssue(accountId, username, issueIdOrKey);
        if(!result.isValid()) {
            throw new InvalidResultException("Assignment error", "Issue has not been updated");
        }

    }


    @Override
    public void onSuccess() {
        super.onSuccess();
        showNotification("Assignment successful", "Issue assignee has been updated");
    }

}
