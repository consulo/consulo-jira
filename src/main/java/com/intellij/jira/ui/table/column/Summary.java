package com.intellij.jira.ui.table.column;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.renders.JiraIssueStringCellRenderer;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.TableCellRenderer;

public class Summary extends StringColumn<JiraIssue> {

    public static final Summary INSTANCE = new Summary();

    private Summary() {
        super("Default.Summary", "Summary", true);
    }

    @Override
    public @Nullable String valueOf(JiraIssue issue) {
        return issue.getSummary();
    }

    @Override
    public @Nullable String getMaxStringValue() {
        return null;
    }

    @Override
    public int getAdditionalWidth() {
        return 700;
    }

    @Override
    public TableCellRenderer getRenderer(JiraIssue issue) {
        return new JiraIssueStringCellRenderer(issue);
    }
}
