package com.intellij.jira.ui.panels;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.ui.AbstractIssuesUi;
import consulo.disposer.Disposable;
import org.jetbrains.annotations.NotNull;

public class JiraFilteredIssuesPanel extends JiraIssuesPanel {

    public JiraFilteredIssuesPanel(@NotNull JiraIssuesData issuesData, @NotNull AbstractIssuesUi issuesUi, @NotNull Disposable parent) {
        super(issuesData, issuesUi, parent);
    }
}
