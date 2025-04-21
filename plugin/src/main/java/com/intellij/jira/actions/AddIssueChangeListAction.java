package com.intellij.jira.actions;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.components.JiraNotificationManager;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.settings.ChangelistSettings;
import com.intellij.jira.settings.ChangelistState;
import com.intellij.jira.util.ChangelistUtil;
import consulo.project.Project;
import consulo.project.ui.notification.Notifications;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.versionControlSystem.change.ChangeListManager;
import consulo.versionControlSystem.change.LocalChangeList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AddIssueChangeListAction extends AnAction {

    public AddIssueChangeListAction() {
        super("New Changelist");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(Project.KEY);
        if (Objects.isNull(project)) {
            return;
        }

        ChangelistSettings settings = ChangelistSettings.getInstance();
        ChangelistState state = settings.getState();
        JiraIssue issue = e.getRequiredData(JiraDataKeys.ISSUE);

        String changelistName = ChangelistUtil.getChangelistName(issue, settings);

        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        LocalChangeList localChangeList = changeListManager.addChangeList(changelistName, "");

        if (state.isActive()) {
            changeListManager.setDefaultChangeList(localChangeList);
        }

        Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotification("Created " + localChangeList.getName() + " Changelist", ""));
    }

}
