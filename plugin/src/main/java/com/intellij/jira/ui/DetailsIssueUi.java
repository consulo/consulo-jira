package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.ui.panels.JiraIssuePanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

public class DetailsIssueUi implements IssueUi {

    private final JiraIssuesData myIssuesData;
    private final String myIssueKey;

    public DetailsIssueUi(JiraIssuesData myIssuesData, String issueKey) {
        this.myIssuesData = myIssuesData;
        this.myIssueKey = issueKey;
    }

    @Override
    public @NotNull String getId() {
        return ""; // TabGroupId provides the id
    }

    @Override
    public @NotNull JComponent getMainComponent() {
        return new JiraIssuePanel(myIssuesData, myIssueKey, this);
    }

    @Override
    public void dispose() {
        System.out.println("Disposing DetailsIssueUi...");
    }
}
