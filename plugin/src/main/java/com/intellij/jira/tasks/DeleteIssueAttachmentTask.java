package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import consulo.application.progress.ProgressIndicator;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

public class DeleteIssueAttachmentTask extends AbstractBackgroundableTask {

    private String attachmentId;

    public DeleteIssueAttachmentTask(@Nonnull Project project, @Nonnull String issueKey, @Nonnull String attachmentId) {
        super(project, "Deleting attachment...", issueKey);
        this.attachmentId = attachmentId;
    }

    @Override
    public void run(@Nonnull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.deleteIssueAttachment(attachmentId);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue comment has not been deleted");
        }

    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        showNotification("Jira", "Attachment deleted successfully");
    }

}
