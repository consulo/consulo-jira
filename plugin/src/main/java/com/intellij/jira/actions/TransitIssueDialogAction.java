package com.intellij.jira.actions;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueTransition;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.IssueTransitionDialog;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import jakarta.annotation.Nonnull;

import java.util.List;

import static com.intellij.jira.rest.model.JiraPermissionType.BROWSE_PROJECTS;
import static com.intellij.jira.rest.model.JiraPermissionType.TRANSITION_ISSUES;
import static java.util.Objects.nonNull;

public class TransitIssueDialogAction extends JiraIssueDialogAction {
    private static final ActionProperties properties = ActionProperties.of("Transit", PlatformIconGroup.actionsForward());

    public TransitIssueDialogAction() {
        super(properties);
    }

    @Override
    public void onClick(@Nonnull AnActionEvent e, @Nonnull Project project, @Nonnull JiraRestApi jiraRestApi) {
        JiraIssue issue = e.getRequiredData(JiraDataKeys.ISSUE);

        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issue.getKey(), BROWSE_PROJECTS, TRANSITION_ISSUES);
        if (!userHasPermission) {
            throw new InvalidPermissionException("Transition Failed", "You don't have permission to transit issues");
        }

        List<JiraIssueTransition> transitions = jiraRestApi.getTransitions(issue.getId());

        IssueTransitionDialog dialog = new IssueTransitionDialog(project, issue, transitions);
        dialog.show();
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(e.getData(JiraDataKeys.ISSUE)));
    }

}
