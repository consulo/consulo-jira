package com.intellij.jira.actions;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.DeleteCommentDialog;
import consulo.application.AllIcons;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import static com.intellij.jira.rest.model.JiraPermissionType.*;
import static java.util.Objects.nonNull;

public class DeleteCommentDialogAction extends JiraIssueDialogAction {

    private static final ActionProperties properties = ActionProperties.of("Delete comment",  AllIcons.General.Remove);

    public DeleteCommentDialogAction() {
        super(properties);
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        String issueKey = e.getRequiredData(JiraDataKeys.ISSUE_KEY);
        JiraIssueComment commentToDelete = e.getRequiredData(JiraDataKeys.ISSUE_COMMENT);

        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, BROWSE_PROJECTS, DELETE_ALL_COMMENTS);
        if(!userHasPermission){
            userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, BROWSE_PROJECTS, DELETE_OWN_COMMENTS);
            if(!userHasPermission){
                throw new InvalidPermissionException("Delete Comment Failed", "You don't have permission to delete comments");
            }

            if(!commentToDelete.getAuthor().getName().equals(jiraRestApi.getUsername())){
                throw new InvalidPermissionException("Delete Comment Failed", "This comment not yours. You cannot delete it");
            }
        }

        DeleteCommentDialog commentDialog = new DeleteCommentDialog(project, issueKey, commentToDelete.getId());
        commentDialog.show();
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(e.getData(JiraDataKeys.ISSUE_COMMENT)));
    }

}
