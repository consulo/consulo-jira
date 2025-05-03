package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import consulo.application.progress.ProgressIndicator;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

import java.io.File;

public class AddIssueAttachmentTask extends AbstractBackgroundableTask {

    private File attachment;

    public AddIssueAttachmentTask(@Nonnull Project project, @Nonnull String issueKey, @Nonnull File attachment) {
        super(project, "Adding issue attachment...", issueKey);
        this.attachment = attachment;
    }

    @Override
    public void run(@Nonnull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.addIssueAttachment(issueIdOrKey, attachment);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue attachment has not been added");
        }

    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        showNotification("Jira", "Issue attachment added successfully");
    }

}
