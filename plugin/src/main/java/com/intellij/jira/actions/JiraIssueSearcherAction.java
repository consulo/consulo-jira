package com.intellij.jira.actions;

import com.intellij.jira.JiraUiDataKeys;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.tree.SearcherTreeNode;
import consulo.ui.ex.action.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.nonNull;

public abstract class JiraIssueSearcherAction extends JiraIssueAction {

    public JiraIssueSearcherAction(@NotNull ActionProperties actionProperties) {
        super(actionProperties);
    }

    @NotNull
    protected JQLSearcher getSearcher(@NotNull AnActionEvent e) {
        return e.getRequiredData(JiraUiDataKeys.SEARCHER_TREE_NODE).getSearcher();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        SearcherTreeNode node = e.getData(JiraUiDataKeys.SEARCHER_TREE_NODE);
        e.getPresentation().setEnabled(isEnabled(node));
    }

    protected boolean isEnabled(SearcherTreeNode node) {
        return nonNull(node) && node.isEditable() && nonNull(node.getSearcher());
    }
}
