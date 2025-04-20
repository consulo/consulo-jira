package com.intellij.jira.ui;

import com.intellij.jira.data.Issues;
import com.intellij.jira.ui.table.JiraIssueTable;
import org.jetbrains.annotations.NotNull;

public interface IssuesUi extends JiraUi {

    @NotNull
    JiraIssueTable getTable();

    IssuesFilterUi getFilterUi();

    @NotNull
    Issues getIssues();

}
