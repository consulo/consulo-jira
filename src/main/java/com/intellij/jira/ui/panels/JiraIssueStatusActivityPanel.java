package com.intellij.jira.ui.panels;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.JiraIssue;
import consulo.ui.ex.awt.OnePixelSplitter;
import org.jetbrains.annotations.NotNull;

public class JiraIssueStatusActivityPanel extends OnePixelSplitter {

    private final JiraIssueStatusPanel myStatusPanel;
    private final JiraIssueActivityPanel myActivityPanel;

    public JiraIssueStatusActivityPanel(@NotNull JiraIssuesData issuesData, JiraIssue issue) {
        super(0.35f);

        myStatusPanel = new JiraIssueStatusPanel(issuesData, issue);
        myActivityPanel = new JiraIssueActivityPanel(issuesData, issue);

        setFirstComponent(myStatusPanel);
        setSecondComponent(myActivityPanel);
    }

    public void update(@NotNull JiraIssue issue) {
        myStatusPanel.update(issue);
        myActivityPanel.update(issue);
    }
}
