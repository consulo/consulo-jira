package com.intellij.jira.ui.renders;

import com.intellij.jira.ui.table.JiraIssueTable;
import consulo.ui.ex.awt.ColoredTableCellRenderer;
import consulo.ui.ex.awt.TableCellState;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.util.Objects;

public class JiraIssueStringCellRenderer extends ColoredTableCellRenderer {

    public JiraIssueStringCellRenderer() {
        setCellState(new BorderlessTableCellState());
    }

    @Override
    protected void customizeCellRenderer(@Nonnull JTable table, @Nullable Object value, boolean selected, boolean hasFocus, int row, int column) {
        if (Objects.nonNull(value) && table instanceof JiraIssueTable) {

            JiraIssueTable myTable = (JiraIssueTable) table;
            append(value.toString(), myTable.applyHighlighter(this, selected, hasFocus, row, column));

        }
    }

    public static class BorderlessTableCellState extends TableCellState {

    }

}
