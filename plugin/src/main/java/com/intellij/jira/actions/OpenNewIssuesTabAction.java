package com.intellij.jira.actions;

import com.intellij.jira.JiraTabsManager;
import com.intellij.jira.JiraUiDataKeys;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.tree.SearcherTreeNode;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import jakarta.annotation.Nonnull;

import static java.util.Objects.nonNull;

public class OpenNewIssuesTabAction extends JiraIssueSearcherAction {
    private static final ActionProperties properties = ActionProperties.of("Open New Filtered Issues Tab", PlatformIconGroup.generalOpennewtab());

    public OpenNewIssuesTabAction() {
        super(properties);
    }

    @Override
    public void actionPerformed(@Nonnull AnActionEvent e) {
        Project project = e.getData(Project.KEY);
        if (nonNull(project)) {
            JQLSearcher searcher = getSearcher(e);

            JiraTabsManager.getInstance(project).openFilteredIssuesTab(searcher);
        }
    }

    @Override
    public void update(@Nonnull AnActionEvent e) {
        SearcherTreeNode node = e.getData(JiraUiDataKeys.SEARCHER_TREE_NODE);
        e.getPresentation().setEnabled(nonNull(node) && nonNull(node.getSearcher()));
    }
}
