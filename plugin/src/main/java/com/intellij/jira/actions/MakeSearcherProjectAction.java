package com.intellij.jira.actions;

import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.tree.SearcherTreeNode;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import jakarta.annotation.Nonnull;

public class MakeSearcherProjectAction extends JiraIssueSearcherAction {

    private static final ActionProperties properties = ActionProperties.of("Make Project", PlatformIconGroup.actionsImport());

    public MakeSearcherProjectAction() {
        super(properties);
    }
    @Override
    public void actionPerformed(@Nonnull AnActionEvent e) {
        JQLSearcher searcher = getSearcher(e);
        searcher.setShared(false);

        JQLSearcherManager.getInstance().moveToProject(e.getData(Project.KEY), searcher);
    }

    @Override
    protected boolean isEnabled(SearcherTreeNode node) {
        return super.isEnabled(node) && node.getSearcher().isShared();
    }
}
