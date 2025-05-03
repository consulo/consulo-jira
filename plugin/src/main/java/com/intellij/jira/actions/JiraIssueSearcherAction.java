package com.intellij.jira.actions;

import com.intellij.jira.JiraUiDataKeys;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.tree.SearcherTreeNode;
import consulo.ui.ex.action.AnActionEvent;
import jakarta.annotation.Nonnull;

import static java.util.Objects.nonNull;

public abstract class JiraIssueSearcherAction extends JiraIssueAction {

    public JiraIssueSearcherAction(@Nonnull ActionProperties actionProperties) {
        super(actionProperties);
    }

    @Nonnull
    protected JQLSearcher getSearcher(@Nonnull AnActionEvent e) {
        return e.getRequiredData(JiraUiDataKeys.SEARCHER_TREE_NODE).getSearcher();
    }

    @Override
    public void update(@Nonnull AnActionEvent e) {
        SearcherTreeNode node = e.getData(JiraUiDataKeys.SEARCHER_TREE_NODE);
        e.getPresentation().setEnabled(isEnabled(node));
    }

    protected boolean isEnabled(SearcherTreeNode node) {
        return nonNull(node) && node.isEditable() && nonNull(node.getSearcher());
    }
}
