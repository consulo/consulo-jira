package com.intellij.jira.actions;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueUser;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.popup.JiraIssueAssignableUsersPopup;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.Project;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.popup.ListPopup;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.jira.rest.model.JiraPermissionType.BROWSE_PROJECTS;
import static com.intellij.jira.rest.model.JiraPermissionType.EDIT_ISSUES;
import static java.util.Objects.nonNull;

public class JiraIssueAssigneePopupAction extends JiraIssuePopupAction {
    private static final ActionProperties properties = ActionProperties.of("Assign", PlatformIconGroup.actionsLoginavatar());

    public JiraIssueAssigneePopupAction() {
        super(properties);
    }

    @Override
    public void onClick(@NotNull AnActionEvent e, @NotNull Project project, @NotNull JiraRestApi jiraRestApi) {
        JiraIssue issue = e.getRequiredData(JiraDataKeys.ISSUE);
        boolean userHasPermission = jiraRestApi.userHasPermissionOnIssue(issue.getKey(), BROWSE_PROJECTS, EDIT_ISSUES);
        if(!userHasPermission){
            throw new InvalidPermissionException("Edit Issue Failed", "You don't have permission to change asignment");
        }

        List<JiraIssueUser> assignableUsers = jiraRestApi.getIssueAssignableUsers(issue.getKey());
        ListPopup popup = JiraIssueAssignableUsersPopup.create(createActionGroup(assignableUsers, issue), project, getComponent());
        popup.showInCenterOf(getComponent());
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(e.getData(JiraDataKeys.ISSUE)));
    }

    private ActionGroup createActionGroup(List<JiraIssueUser> assignableUsers, JiraIssue issue){
        JiraIssueActionGroup group = new JiraIssueActionGroup(getComponent());
        assignableUsers.forEach(u -> group.add(JiraIssueAssignmentExecuteAction.assignUser(u.getDisplayName(), u.getAccountId(), u.getName(), issue.getKey())));
        group.add(JiraIssueAssignmentExecuteAction.assignAnyone(issue.getKey()));


        return group;
    }

}
