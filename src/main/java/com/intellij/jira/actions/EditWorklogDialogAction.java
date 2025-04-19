package com.intellij.jira.actions;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.EditWorklogDialog;
import consulo.application.AllIcons;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.intellij.jira.rest.model.JiraPermissionType.*;
import static java.util.Objects.nonNull;

public class EditWorklogDialogAction extends JiraIssueDialogAction {
    private static final ActionProperties properties = ActionProperties.of("Edit Work Log", AllIcons.Actions.Edit);

    public EditWorklogDialogAction() {
        super(properties);
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        String issueKey = e.getRequiredData(JiraDataKeys.ISSUE_KEY);
        JiraIssueWorklog issueWorklog = e.getRequiredData(JiraDataKeys.ISSUE_WORKLOG);
        JiraIssueWorklog worklogToEdit = jiraRestApi.getWorklog(issueKey, issueWorklog.getId());
        // Check permissions
        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, BROWSE_PROJECTS, EDIT_ALL_WORKLOGS);
        if (!userHasPermission) {
            userHasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, BROWSE_PROJECTS, EDIT_OWN_WORKLOGS);
            if (!userHasPermission) {
                throw new InvalidPermissionException("Edit Work Log Failed", "You don't have permission to edit work logs");
            }

            if (nonNull(worklogToEdit)
                    && !jiraRestApi.getUsername().equals(worklogToEdit.getAuthor().getName())
                    && !jiraRestApi.getUsername().equals(worklogToEdit.getAuthor().getEmailAddress())) {
                throw new InvalidPermissionException("Edit Work Log Failed", "This work log not yours. You cannot edit it.");
            }
        }

        if (Objects.nonNull(worklogToEdit)) {
            String projectKey = e.getRequiredData(JiraDataKeys.PROJECT_KEY);
            List<String> projectRoles = jiraRestApi.getProjectRoles(projectKey);

            JiraIssueTimeTracking issueTimeTracking = e.getRequiredData(JiraDataKeys.ISSUE_TIME_TRACKING);
            EditWorklogDialog dialog = new EditWorklogDialog(project, issueKey, projectRoles, worklogToEdit, issueTimeTracking, false);
            dialog.show();
        }
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(e.getData(JiraDataKeys.ISSUE_WORKLOG)));
    }

}
