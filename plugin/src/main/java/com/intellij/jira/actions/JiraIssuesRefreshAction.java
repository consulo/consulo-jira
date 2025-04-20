package com.intellij.jira.actions;

import com.intellij.jira.JiraUiDataKeys;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.AbstractIssuesUi;
import consulo.application.ApplicationManager;
import consulo.project.Project;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class JiraIssuesRefreshAction extends AnAction {


    @Override
    public void update(AnActionEvent event) {
        Project project = event.getData(Project.KEY);
        if (isNull(project)|| !project.isInitialized() || project.isDisposed()) {
            event.getPresentation().setEnabled(false);
        } else {
            JiraServerManager manager = ApplicationManager.getApplication().getService(JiraServerManager.class);
            event.getPresentation().setEnabled(manager.hasJiraServerConfigured(project));
        }
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(Project.KEY);
        if(nonNull(project)){
            AbstractIssuesUi ui = event.getRequiredData(JiraUiDataKeys.ISSUES_UI);
            ui.refresh();
        }
    }

}
