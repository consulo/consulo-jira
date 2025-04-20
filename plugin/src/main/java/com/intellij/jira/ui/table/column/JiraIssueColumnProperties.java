package com.intellij.jira.ui.table.column;

import com.intellij.jira.ui.JiraIssueUiProperties;
import org.jetbrains.annotations.NotNull;

public class JiraIssueColumnProperties {

    private TableColumnVisibilityProperty visibility;

    public JiraIssueColumnProperties(@NotNull JiraIssueColumn<?, ?> column) {
        visibility = new TableColumnVisibilityProperty(column);
    }

    public TableColumnVisibilityProperty getVisibility() {
        return visibility;
    }

    public static class TableColumnVisibilityProperty extends JiraIssueUiProperties.JiraIssueUiProperty<Boolean> {

        private final JiraIssueColumn<?, ?> column;

        public TableColumnVisibilityProperty(@NotNull JiraIssueColumn<?, ?> column) {
            super(String.format("Table.%s.ColumnIdVisibility", column.getId()));
            this.column = column;
        }

        public JiraIssueColumn<?, ?> getColumn() {
            return column;
        }
    }
}
