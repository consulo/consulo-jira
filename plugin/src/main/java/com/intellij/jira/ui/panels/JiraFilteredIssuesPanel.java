package com.intellij.jira.ui.panels;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.ui.AbstractIssuesUi;
import consulo.disposer.Disposable;
import jakarta.annotation.Nonnull;

public class JiraFilteredIssuesPanel extends JiraIssuesPanel {

    public JiraFilteredIssuesPanel(@Nonnull JiraIssuesData issuesData, @Nonnull AbstractIssuesUi issuesUi, @Nonnull Disposable parent) {
        super(issuesData, issuesUi, parent);
    }
}
