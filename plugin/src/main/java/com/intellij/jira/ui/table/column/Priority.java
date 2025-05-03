package com.intellij.jira.ui.table.column;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.renders.JiraIconAndTextTableCellRenderer;
import com.intellij.jira.util.JiraIssueUtil;
import jakarta.annotation.Nullable;

import javax.swing.table.TableCellRenderer;

import static java.util.Objects.nonNull;

public class Priority extends StringColumn<JiraIssue> {

    public static final Priority INSTANCE = new Priority();

    private final JiraIconAndTextTableCellRenderer myPriorityRenderer = new JiraIconAndTextTableCellRenderer();

    protected Priority() {
        super("Default.IssuePriority", "Priority");
    }

    @Override
    public TableCellRenderer getRenderer(JiraIssue issue) {
        return myPriorityRenderer;
    }

    @Override
    public String valueOf(JiraIssue issue) {
        return JiraIssueUtil.getPriority(issue);
    }

    @Override
    public TableCellRenderer getCustomizedRenderer(JiraIssue issue, TableCellRenderer renderer) {
        if(renderer instanceof JiraIconAndTextTableCellRenderer && nonNull(issue.getPriority())){
            ((JiraIconAndTextTableCellRenderer) renderer).setIconUrl(issue.getPriority().getIconUrl());
            ((JiraIconAndTextTableCellRenderer) renderer).emptyText();
            ((JiraIconAndTextTableCellRenderer) renderer).setToolTipText(valueOf(issue));
        }

        return renderer;
    }

    @Override
    public @Nullable String getMaxStringValue() {
        return "";
    }

    @Override
    public int getAdditionalWidth() {
        return 0;
    }
}
