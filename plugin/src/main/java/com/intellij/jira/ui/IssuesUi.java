package com.intellij.jira.ui;

import com.intellij.jira.data.Issues;
import com.intellij.jira.ui.table.JiraIssueTable;
import jakarta.annotation.Nonnull;

public interface IssuesUi extends JiraUi {

    @Nonnull
    JiraIssueTable getTable();

    IssuesFilterUi getFilterUi();

    @Nonnull
    Issues getIssues();

}
