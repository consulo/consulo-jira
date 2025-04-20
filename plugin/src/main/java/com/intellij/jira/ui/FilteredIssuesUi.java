package com.intellij.jira.ui;

import com.intellij.jira.JiraTabsManager;
import com.intellij.jira.data.Issues;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.listener.SearcherListener;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.panels.JiraFilteredIssuesPanel;
import com.intellij.jira.ui.table.JiraIssueTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

public class FilteredIssuesUi extends AbstractIssuesUi {

    private JQLSearcher mySearcher;
    private final JiraFilteredIssuesPanel myFilteredIssuesPanel;

    public FilteredIssuesUi(JiraIssuesData issuesData, JQLSearcher searcher) {
        super(searcher.getAlias(), issuesData);

        mySearcher = searcher;
        myFilteredIssuesPanel = new JiraFilteredIssuesPanel(issuesData, this, this);
        updateHighlighters();

        issuesData.getProject().getMessageBus().connect()
                .subscribe(JQLSearcherManager.JQL_SEARCHERS_CHANGE, new MySearcherListener());

    }

    @NotNull
    @Override
    public String getId() {
        return mySearcher.getAlias();
    }

    @NotNull
    @Override
    public JComponent getMainComponent() {
        return myFilteredIssuesPanel;
    }

    @NotNull
    @Override
    public JiraIssueTable getTable() {
        return myFilteredIssuesPanel.getJiraIssueTable();
    }

    @NotNull
    @Override
    public JQLSearcher getSearcher() {
        return mySearcher;
    }

    @Override
    void setIssues(Issues issues) {
        myFilteredIssuesPanel.setIssues(issues.get());
    }

    private class MySearcherListener implements SearcherListener {

        @Override
        public void onAdded(JQLSearcher editedSearcher) {
            // Do nothing
        }

        @Override
        public void onChange(JQLSearcher editedSearcher) {
            if (mySearcher.getId().equals(editedSearcher.getId())) {
                mySearcher = editedSearcher;

                // Update tab name
                JiraTabsManager.getInstance(myIssuesData.getProject()).updateTabName(FilteredIssuesUi.this);

                // Refresh
                refresh();
            }
        }

        @Override
        public void onRemoved(JQLSearcher removedSearcher) {
            if (mySearcher.getId().equals(removedSearcher.getId())) {
                JiraTabsManager.getInstance(myIssuesData.getProject()).closeTab(removedSearcher.getAlias());
            }
        }
    }

}
