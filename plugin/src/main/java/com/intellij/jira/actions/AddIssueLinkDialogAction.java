package com.intellij.jira.actions;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueLinkType;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.AddIssueLinkDialog;
import consulo.application.AllIcons;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import jakarta.annotation.Nonnull;

import java.util.List;

import static com.intellij.jira.rest.model.JiraPermissionType.BROWSE_PROJECTS;
import static com.intellij.jira.rest.model.JiraPermissionType.LINK_ISSUES;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class AddIssueLinkDialogAction extends JiraIssueDialogAction {
    private static final ActionProperties properties = ActionProperties.of("New Link",  AllIcons.General.Add);

    public AddIssueLinkDialogAction() {
        super(properties);
    }

    @Override
    public void onClick(@Nonnull AnActionEvent e, @Nonnull Project project, @Nonnull JiraRestApi jiraRestApi) {
        String issueKey = e.getData(JiraDataKeys.ISSUE_KEY);
        if (nonNull(issueKey)) {
            boolean hasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, BROWSE_PROJECTS, LINK_ISSUES);
            if(!hasPermission){
                throw new InvalidPermissionException("Jira", "You don't have permission to create issue links");
            }
        }

        String projectKey = e.getRequiredData(JiraDataKeys.PROJECT_KEY);
        List<JiraIssueLinkType> issueLinkTypes = jiraRestApi.getIssueLinkTypes();
        List<String> issues = jiraRestApi.getIssues("project=" + projectKey).stream().map(JiraIssue::getKey).collect(toList());
        issues.remove(issueKey);

        openIssueLinkDialog(project, issueLinkTypes, issues, issueKey);
    }

    public void openIssueLinkDialog(Project project, List<JiraIssueLinkType> issueLinkTypes, List<String> issues, String issueKey) {
        AddIssueLinkDialog dialog = new AddIssueLinkDialog(project, issueLinkTypes, issues, issueKey);
        dialog.show();
    }

}
