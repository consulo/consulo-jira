package com.intellij.jira.actions;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.AddCommentDialog;
import consulo.application.AllIcons;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import jakarta.annotation.Nonnull;

import java.util.List;

import static com.intellij.jira.rest.model.JiraPermissionType.ADD_COMMENTS;
import static com.intellij.jira.rest.model.JiraPermissionType.BROWSE_PROJECTS;

public class AddCommentDialogAction extends JiraIssueDialogAction{
    private static final ActionProperties properties = ActionProperties.of("Add a comment",  AllIcons.General.Add);

    public AddCommentDialogAction() {
        super(properties);
    }

    @Override
    public void onClick(@Nonnull AnActionEvent e, @Nonnull Project project, @Nonnull JiraRestApi jiraRestApi) {
        String issueKey = e.getRequiredData(JiraDataKeys.ISSUE_KEY);
        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, BROWSE_PROJECTS, ADD_COMMENTS);
        if(!userHasPermission){
            throw new InvalidPermissionException("Add Comment Failed", "You don't have permission to add comments");
        }

        String projectKey = e.getRequiredData(JiraDataKeys.PROJECT_KEY);
        List<String> projectRoles = jiraRestApi.getProjectRoles(projectKey);

        AddCommentDialog commentDialog = new AddCommentDialog(project, issueKey, projectRoles);
        commentDialog.show();
    }

}
