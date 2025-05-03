package com.intellij.jira.ui;

import com.intellij.jira.data.Issues;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.listener.SearcherListener;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.panels.JiraIssuesPanel;
import com.intellij.jira.ui.table.JiraIssueTable;
import jakarta.annotation.Nonnull;

import javax.swing.JComponent;

public class DefaultIssuesUi extends AbstractIssuesUi {

    private static final String ISSUES_ID = "Issues";

    protected final JiraIssuesPanel myIssuesPanel;

    public DefaultIssuesUi(JiraIssuesData issuesData) {
        super(ISSUES_ID, issuesData);

        myIssuesPanel = new JiraIssuesPanel(issuesData, this, this);
        updateHighlighters();

        issuesData.getProject().getMessageBus().connect()
                .subscribe(JQLSearcherManager.JQL_SEARCHERS_CHANGE, new MySearcherListener());
    }

    @Nonnull
    @Override
    public JComponent getMainComponent() {
        return myIssuesPanel;
    }

    @Nonnull
    @Override
    public JiraIssueTable getTable() {
        return myIssuesPanel.getJiraIssueTable();
    }

    @Nonnull
    @Override
    public JQLSearcher getSearcher() {
        return JQLSearcherManager.getInstance().getSelectedSearcher(myIssuesData.getProject());
    }

    @Override
    void setIssues(Issues issues) {
        myIssuesPanel.setIssues(issues.get());
    }

    private class MySearcherListener implements SearcherListener {

        @Override
        public void onAdded(JQLSearcher editedSearcher) {
            // Do nothing
        }

        @Override
        public void onChange(JQLSearcher searcher) {
            refresh();
        }

        @Override
        public void onRemoved(JQLSearcher removedSearcher) {
            refresh();
        }
    }

}
