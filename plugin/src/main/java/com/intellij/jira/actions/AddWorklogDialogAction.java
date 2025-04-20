package com.intellij.jira.actions;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.AddWorklogDialog;
import consulo.application.AllIcons;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.jira.rest.model.JiraPermissionType.BROWSE_PROJECTS;
import static com.intellij.jira.rest.model.JiraPermissionType.WORK_ON_ISSUES;

public class AddWorklogDialogAction extends JiraIssueDialogAction {

    private static final ActionProperties properties = ActionProperties.of("Add Log Work",  AllIcons.General.Add);

    public AddWorklogDialogAction() {
        super(properties);
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        String issueKey = e.getRequiredData(JiraDataKeys.ISSUE_KEY);
        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, BROWSE_PROJECTS, WORK_ON_ISSUES);
        if(!userHasPermission){
            throw new InvalidPermissionException("Add Log Work Failed", "You don't have permission to log work");
        }

        String projectKey = e.getRequiredData(JiraDataKeys.PROJECT_KEY);
        List<String> projectRoles = jiraRestApi.getProjectRoles(projectKey);

        JiraIssueTimeTracking issueTimeTracking = e.getRequiredData(JiraDataKeys.ISSUE_TIME_TRACKING);
        AddWorklogDialog dialog = new AddWorklogDialog(project, issueKey, projectRoles, issueTimeTracking);
        dialog.show();
    }
}
