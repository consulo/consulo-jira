package com.intellij.jira.actions;

import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import consulo.application.AllIcons;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class DeleteSearcherAction extends JiraIssueSearcherAction {

    private static final ActionProperties properties = ActionProperties.of("Delete Searcher", AllIcons.Actions.GC);

    public DeleteSearcherAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JQLSearcher searcher = getSearcher(e);

        JQLSearcherManager.getInstance().remove(e.getData(Project.KEY), searcher.getId(), searcher);
    }

}
