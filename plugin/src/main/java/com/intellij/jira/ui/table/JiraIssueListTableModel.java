package com.intellij.jira.ui.table;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.table.column.JiraIssueColumn;
import com.intellij.jira.ui.table.column.JiraIssueColumnManager;
import com.intellij.jira.ui.table.column.JiraIssueColumnUtils;
import consulo.ui.ex.awt.ColumnInfo;
import consulo.ui.ex.awt.table.ListTableModel;
import consulo.util.lang.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class JiraIssueListTableModel extends ListTableModel<JiraIssue> {

    public JiraIssueListTableModel() {
        super();
        setColumnInfos(getIssueColumns());
    }

    public void update() {
       setIssues(new ArrayList<>(getItems()));
    }

    public void setIssues(List<JiraIssue> issues) {
        initializeColumnsWidth(issues);
        setColumnInfos(getIssueColumns());
        setItems(issues);
    }

    public void initializeColumnsWidth(List<JiraIssue> issues) {
        issues.forEach(issue -> {
            for (JiraIssueColumn<?, ?> column : getJiraIssueColumnManager().getCurrentColumns()) {
                String columnWidth = getJiraIssueColumnManager().getColumnWidth(column.getId());
                String valueColumnWidth = StringUtil.notNullize(((JiraIssueColumn<JiraIssue, String>) column).valueOf(issue));
                if (columnWidth.length() < valueColumnWidth.length()) {
                    getJiraIssueColumnManager().setColumnWidth(column.getId(), valueColumnWidth);
                }
            }
        });
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }


    private JiraIssueColumnManager getJiraIssueColumnManager() {
        return JiraIssueColumnManager.getInstance();
    }

    private ColumnInfo<?, ?>[] getIssueColumns() {
        return JiraIssueColumnUtils.getVisibleColumns().toArray(new ColumnInfo[0]);
    }

}
