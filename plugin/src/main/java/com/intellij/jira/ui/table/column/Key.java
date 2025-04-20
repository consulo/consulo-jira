package com.intellij.jira.ui.table.column;

import com.intellij.jira.rest.model.JiraIssue;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.TableCellRenderer;

public class Key extends StringColumn<JiraIssue> {

    public static final Key INSTANCE = new Key();

    private Key() {
        super("Default.Key", "Key");
    }

    @Override
    public @Nullable String valueOf(JiraIssue issue) {
        return issue.getKey();
    }

    @Override
    public TableCellRenderer getRenderer(JiraIssue issue) {
        return DEFAULT_JIRA_ISSUE_STRING_CELL_RENDERER;
    }
}
