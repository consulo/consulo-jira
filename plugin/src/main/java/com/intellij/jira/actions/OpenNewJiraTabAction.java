package com.intellij.jira.actions;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.JiraTabsManager;
import com.intellij.jira.rest.model.JiraIssue;
import consulo.application.AllIcons;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static java.util.Objects.nonNull;

public class OpenNewJiraTabAction extends JiraIssueAction {

    private static final ActionProperties properties = ActionProperties.of("Open Issue in Tab", AllIcons.Actions.OpenNewTab);

    public OpenNewJiraTabAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(Project.KEY);
        if (Objects.nonNull(project)) {
            JiraIssue issue = e.getRequiredData(JiraDataKeys.ISSUE);
            JiraTabsManager.getInstance(project).openDetailsIssueTab(issue.getKey());
        }

    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(e.getData(JiraDataKeys.ISSUE)));
    }
}
