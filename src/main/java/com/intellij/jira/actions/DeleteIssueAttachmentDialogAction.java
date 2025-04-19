package com.intellij.jira.actions;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssueAttachment;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.DeleteIssueAttachmentDialog;
import consulo.application.AllIcons;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import static com.intellij.jira.rest.model.JiraPermissionType.*;
import static java.util.Objects.nonNull;

public class DeleteIssueAttachmentDialogAction extends JiraIssueDialogAction {

    private static final ActionProperties properties = ActionProperties.of("Delete Attachment",  AllIcons.General.Remove);

    public DeleteIssueAttachmentDialogAction() {
        super(properties);
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        String issueKey = e.getRequiredData(JiraDataKeys.ISSUE_KEY);
        boolean hasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, BROWSE_PROJECTS, DELETE_ALL_ATTACHMENTS);
        if(!hasPermission){
            hasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, BROWSE_PROJECTS, DELETE_OWN_ATTACHMENTS);
            if (!hasPermission) {
                throw new InvalidPermissionException("Jira", "You don't have permission to delete attached files");
            }
        }

        JiraIssueAttachment issueAttachment = e.getRequiredData(JiraDataKeys.ISSUE_ATTACHMENT);
        DeleteIssueAttachmentDialog dialog = new DeleteIssueAttachmentDialog(project, issueKey, issueAttachment.getId());
        dialog.show();
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(e.getData(JiraDataKeys.ISSUE_ATTACHMENT)));
    }

}
