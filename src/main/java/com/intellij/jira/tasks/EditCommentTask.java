package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import consulo.application.progress.ProgressIndicator;
import consulo.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EditCommentTask extends AbstractBackgroundableTask {

    private String commentId;
    private String body;
    private String viewableBy;

    public EditCommentTask(@Nullable Project project, String issueKey, String commentId, String body, String viewableBy) {
        super(project, "Editing comment", issueKey);
        this.commentId = commentId;
        this.body = body;
        this.viewableBy = viewableBy;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.editIssueComment(issueIdOrKey, commentId, body, viewableBy);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue comment has not been updated");
        }

    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        showNotification("Jira", "Comment edited successfully");
    }

}
