package com.intellij.jira.ui.renders;

import com.intellij.jira.ui.panels.JiraPanel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;

import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import static com.intellij.jira.util.JiraLabelUtil.IN_PROGRESS_TEXT_COLOR;
import static java.awt.BorderLayout.LINE_START;

public class JiraIssueStatusTableCellRenderer extends JiraIssueTableCellRenderer {

    private String statusName;
    private Color statusCategoryColor;
    private boolean isInProgressCategory;

    public JiraIssueStatusTableCellRenderer(String statusName, Color statusCategoryColor, boolean isInProgressCategory) {
        super();
        this.statusName = statusName;
        this.statusCategoryColor = statusCategoryColor;
        this.isInProgressCategory = isInProgressCategory;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        JiraPanel panel = new JiraPanel(new BorderLayout()).withBackground(getBackground());

        setText(StringUtil.toUpperCase(statusName));
        setBackground(statusCategoryColor);
        setForeground(isInProgressCategory ? IN_PROGRESS_TEXT_COLOR : JBColor.WHITE);
        setFont(JBFont.create(new Font("SansSerif", Font.BOLD, 8)));
        setBorder(JBUI.Borders.empty(4, 5));

        panel.setBorder(JBUI.Borders.empty(4, 3));
        panel.add(this, LINE_START);

        return panel;
    }

}
