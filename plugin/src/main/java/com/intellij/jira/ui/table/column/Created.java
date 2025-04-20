package com.intellij.jira.ui.table.column;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.util.JiraIssueUtil;

import javax.swing.table.TableCellRenderer;

public class Created extends StringColumn<JiraIssue> {

    public static final Created INSTANCE = new Created();

    private Created() {
        super("Dynamic.IssueCreated", "Created", false, true);
    }

    @Override
    public String valueOf(JiraIssue issue) {
        return JiraIssueUtil.getCreated(issue);
    }

    @Override
    public TableCellRenderer getRenderer(JiraIssue issue) {
        return DEFAULT_JIRA_ISSUE_STRING_CELL_RENDERER;
    }

}
