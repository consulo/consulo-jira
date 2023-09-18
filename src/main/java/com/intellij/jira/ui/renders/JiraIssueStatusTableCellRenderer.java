package com.intellij.jira.ui.renders;

import com.intellij.jira.rest.model.JiraIssueStatus;
import com.intellij.jira.ui.panels.JiraPanel;
import com.intellij.jira.util.JiraBorders;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.ui.components.JBLabel;

import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Component;

import static java.awt.BorderLayout.LINE_START;

public class JiraIssueStatusTableCellRenderer extends JiraIssueTableCellRenderer {

    private final JiraIssueStatus jiraIssueStatus;

    public JiraIssueStatusTableCellRenderer(JiraIssueStatus jiraIssueStatus) {
        super();
        this.jiraIssueStatus = jiraIssueStatus;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        JiraPanel panel = new JiraPanel(new BorderLayout()).withBackground(getBackground());
        JBLabel jiraStatusLabel = JiraLabelUtil.createStatusLabel(jiraIssueStatus);

        panel.setBorder(JiraBorders.empty(4, 3));
        panel.add(jiraStatusLabel, LINE_START);

        return panel;
    }

}
