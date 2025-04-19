package com.intellij.jira.ui.renders;

import com.intellij.jira.ui.table.JiraIssueTable;
import consulo.ui.ex.awt.ColoredTableCellRenderer;
import consulo.ui.ex.awt.TableCellState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.util.Objects;

public class JiraIssueStringCellRenderer extends ColoredTableCellRenderer {

    public JiraIssueStringCellRenderer() {
        setCellState(new BorderlessTableCellState());
    }

    @Override
    protected void customizeCellRenderer(@NotNull JTable table, @Nullable Object value, boolean selected, boolean hasFocus, int row, int column) {
        if (Objects.nonNull(value) && table instanceof JiraIssueTable) {

            JiraIssueTable myTable = (JiraIssueTable) table;
            append(value.toString(), myTable.applyHighlighter(this, selected, hasFocus, row, column));

        }
    }

    public static class BorderlessTableCellState extends TableCellState {

    }

}
