package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.ui.panels.JiraIssuePanel;
import jakarta.annotation.Nonnull;

import javax.swing.JComponent;

public class DetailsIssueUi implements IssueUi {

    private final JiraIssuesData myIssuesData;
    private final String myIssueKey;

    public DetailsIssueUi(JiraIssuesData myIssuesData, String issueKey) {
        this.myIssuesData = myIssuesData;
        this.myIssueKey = issueKey;
    }

    @Override
    public @Nonnull String getId() {
        return ""; // TabGroupId provides the id
    }

    @Override
    public @Nonnull JComponent getMainComponent() {
        return new JiraIssuePanel(myIssuesData, myIssueKey, this);
    }

    @Override
    public void dispose() {
        System.out.println("Disposing DetailsIssueUi...");
    }
}
