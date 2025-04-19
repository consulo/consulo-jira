package com.intellij.jira.ui.table.column;

import com.intellij.jira.ui.renders.JiraIssueStringCellRenderer;
import com.intellij.jira.ui.renders.JiraIssueTableCellRenderer;
import consulo.ui.ex.awt.UIUtil;

import javax.swing.table.TableCellRenderer;

public abstract class StringColumn<T> extends JiraIssueColumn<T, String> {

    public final TableCellRenderer DEFAULT_JIRA_ISSUE_TABLE_CELL_RENDERER = new JiraIssueTableCellRenderer();
    public final TableCellRenderer DEFAULT_JIRA_ISSUE_STRING_CELL_RENDERER = new JiraIssueStringCellRenderer();

    protected StringColumn(String id, String name) {
        super(id, name);
    }

    protected StringColumn(String id, String name, boolean resizable) {
        super(id, name, resizable);
    }

    protected StringColumn(String id, String name, boolean resizable, boolean hideable) {
        super(id, name, resizable, hideable);
    }

    @Override
    public TableCellRenderer getRenderer(T t) {
        return DEFAULT_JIRA_ISSUE_TABLE_CELL_RENDERER;
    }

    @Override
    public int getAdditionalWidth() {
        return UIUtil.DEFAULT_VGAP;
    }
}
