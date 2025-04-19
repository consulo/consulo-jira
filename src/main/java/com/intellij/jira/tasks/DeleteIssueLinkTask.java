package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import consulo.application.progress.ProgressIndicator;
import consulo.project.Project;
import org.jetbrains.annotations.NotNull;

public class DeleteIssueLinkTask extends AbstractBackgroundableTask {

    private String issueLinkId;

    public DeleteIssueLinkTask(@NotNull Project project, String issueKey, String issueLinkId) {
        super(project, "Deleting comment...", issueKey);
        this.issueLinkId = issueLinkId;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.deleteIssueLink(issueLinkId);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue link has not been deleted");
        }

    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        showNotification("Jira", "Issue Link deleted successfully");
    }

}
