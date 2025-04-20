package com.intellij.jira.actions;

import com.intellij.jira.ui.dialog.ConfigureJiraServersDialog;
import consulo.application.AllIcons;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.isNull;

public class ConfigureJiraServersAction extends JiraIssueAction {

    private static final ActionProperties properties = ActionProperties.of("Configure Servers...",  AllIcons.General.Settings);

    public ConfigureJiraServersAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(Project.KEY);
        if(isNull(project)){
            return;
        }

        ConfigureJiraServersDialog dlg = new ConfigureJiraServersDialog(project);
        dlg.show();
    }
}
