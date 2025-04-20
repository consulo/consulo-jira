package com.intellij.jira.ui.table.column;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.renders.JiraIssueStatusTableCellRenderer;
import com.intellij.jira.util.JiraIssueUtil;

import javax.swing.table.TableCellRenderer;

public class Status extends StringColumn<JiraIssue> {

    public static final Status INSTANCE = new Status();

    protected Status() {
        super("Default.IssueStatus", "Status");
    }

    @Override
    public String valueOf(JiraIssue issue) {
        return JiraIssueUtil.getStatus(issue);
    }

    @Override
    public TableCellRenderer getRenderer(JiraIssue issue) {
        return new JiraIssueStatusTableCellRenderer(issue.getStatus());
    }

}
