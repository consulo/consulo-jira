package com.intellij.jira.actions;

import com.intellij.jira.JiraUiDataKeys;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.panels.JiraIssuesPanel;
import com.intellij.jira.ui.popup.GoToIssuePopup;
import consulo.application.AllIcons;
import consulo.application.ApplicationManager;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

public class GoToIssuePopupAction extends JiraIssueAction {
    private static final ActionProperties properties = ActionProperties.of("Go to",  AllIcons.Actions.Find, "control shift G");

    public GoToIssuePopupAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(Project.KEY);
        if(isNull(project)){
            return;
        }

        JiraIssuesPanel issuesPanel = e.getRequiredData(JiraUiDataKeys.ISSUES_PANEL);

        List<String> issueKeys = issuesPanel.getJiraIssueTable().getModel().getItems().stream().map(JiraIssue::getKey).collect(toList());
        GoToIssuePopup popup = new GoToIssuePopup(project, issueKeys, key -> issuesPanel.goToIssue(key));
        popup.show(issuesPanel.getJiraIssueTable());
    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getData(Project.KEY);
        if (isNull(project)|| !project.isInitialized() || project.isDisposed()) {
            e.getPresentation().setEnabled(false);
        } else {
            JiraServerManager manager = ApplicationManager.getApplication().getInstance(JiraServerManager.class);
            e.getPresentation().setEnabled(manager.hasJiraServerConfigured(project));
        }
    }
}
