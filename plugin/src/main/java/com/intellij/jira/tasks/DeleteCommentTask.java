package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import consulo.application.progress.ProgressIndicator;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

public class DeleteCommentTask extends AbstractBackgroundableTask {

    private String commentId;

    public DeleteCommentTask(@Nonnull Project project, String issueKey, String commentId) {
        super(project, "Deleting comment...", issueKey);
        this.commentId = commentId;
    }

    @Override
    public void run(@Nonnull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.deleteIssueComment(issueIdOrKey, commentId);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue comment has not been deleted");
        }

    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        showNotification("Jira", "Comment deleted successfully");
    }

}
