package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import consulo.application.progress.ProgressIndicator;
import consulo.project.Project;
import org.jetbrains.annotations.NotNull;

public class DeleteIssueAttachmentTask extends AbstractBackgroundableTask {

    private String attachmentId;

    public DeleteIssueAttachmentTask(@NotNull Project project, @NotNull String issueKey, @NotNull String attachmentId) {
        super(project, "Deleting attachment...", issueKey);
        this.attachmentId = attachmentId;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
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
