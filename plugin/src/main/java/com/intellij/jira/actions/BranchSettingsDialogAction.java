package com.intellij.jira.actions;

import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.settings.branch.BranchSettings;
import com.intellij.jira.ui.dialog.BranchSettingsDialog;
import consulo.application.AllIcons;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import jakarta.annotation.Nonnull;

public class BranchSettingsDialogAction extends JiraIssueDialogAction {

    private static final ActionProperties properties = ActionProperties.of("Settings...", AllIcons.General.Settings);

    public BranchSettingsDialogAction() {
        super(properties);
    }

    @Override
    public void onClick(@Nonnull AnActionEvent e, @Nonnull Project project, @Nonnull JiraRestApi jiraRestApi) {
        BranchSettingsDialog branchSettingsDialog = new BranchSettingsDialog(project, BranchSettings.getInstance());
        branchSettingsDialog.show();
    }

}
