package com.intellij.jira.actions;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssueLink;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.dialog.DeleteIssueLinkDialog;
import consulo.application.AllIcons;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import static com.intellij.jira.rest.model.JiraPermissionType.BROWSE_PROJECTS;
import static com.intellij.jira.rest.model.JiraPermissionType.LINK_ISSUES;
import static java.util.Objects.nonNull;

public class DeleteIssueLinkDialogAction extends JiraIssueDialogAction {
    private static final ActionProperties properties = ActionProperties.of("Delete link",  AllIcons.General.Remove);

    public DeleteIssueLinkDialogAction() {
        super(properties);
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        String issueKey = e.getRequiredData(JiraDataKeys.ISSUE_KEY);
        boolean hasPermission = jiraRestApi.userHasPermissionOnIssue(issueKey, BROWSE_PROJECTS, LINK_ISSUES);
        if(!hasPermission){
            throw new InvalidPermissionException("Jira", "You don't have permission to delete an issue link");
        }

        JiraIssueLink issueLink = e.getRequiredData(JiraDataKeys.ISSUE_LINK);
        DeleteIssueLinkDialog dialog = new DeleteIssueLinkDialog(project, issueKey, issueLink.getId());
        dialog.show();
    }

    @Override
    public void update(AnActionEvent e) {
       e.getPresentation().setEnabled(nonNull(e.getData(JiraDataKeys.ISSUE_LINK)));
    }

}
