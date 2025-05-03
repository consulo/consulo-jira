package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import consulo.application.progress.ProgressIndicator;
import consulo.project.Project;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class AddCommentTask extends AbstractBackgroundableTask {

    private String body;
    private String viewableBy;

    public AddCommentTask(@Nullable Project project, String issueKey, String body, String viewableBy) {
        super(project, "Adding a comment", issueKey);
        this.body = body;
        this.viewableBy = viewableBy;
    }

    @Override
    public void run(@Nonnull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.addIssueComment(body, issueIdOrKey, viewableBy);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue comment has not been added");
        }

    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        showNotification("Jira", "Comment added successfully");
    }

}
