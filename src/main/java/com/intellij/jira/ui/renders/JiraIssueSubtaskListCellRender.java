package com.intellij.jira.ui.renders;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueStatus;
import com.intellij.jira.ui.panels.JiraPanel;
import com.intellij.jira.util.JiraLabelUtil;
import consulo.ui.ex.awt.JBLabel;
import consulo.ui.ex.awt.JBUI;

import javax.swing.*;
import java.awt.*;

import static com.intellij.jira.util.JiraLabelUtil.*;
import static consulo.util.lang.StringUtil.toUpperCase;

public class JiraIssueSubtaskListCellRender extends DefaultJiraListCellRender {

    private JBLabel keyLabel;
    private JBLabel summaryLabel;
    private JBLabel statusLabel;

    public JiraIssueSubtaskListCellRender() {
        super();
        init();
    }

    private void init() {
        JPanel issuePanel = new JiraPanel(new BorderLayout())
                .withBorder(JBUI.Borders.empty(4, 5)).andTransparent();
        keyLabel =  JiraLabelUtil.createEmptyLabel().withFont(BOLD);
        summaryLabel =  JiraLabelUtil.createEmptyLabel().withBorder(JBUI.Borders.emptyLeft(10));
        statusLabel = JiraLabelUtil.createEmptyStatusLabel();

        issuePanel.add(keyLabel, BorderLayout.LINE_START);
        issuePanel.add(summaryLabel, BorderLayout.CENTER);
        issuePanel.add(statusLabel, BorderLayout.LINE_END);
        add(issuePanel);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        JiraIssue issue = (JiraIssue) value;

        setBorder(JBUI.Borders.emptyBottom(2));
        setBackground(DARCULA_TEXT_COLOR);

        keyLabel.setText(issue.getKey());
        keyLabel.setForeground(JiraLabelUtil.BLACK);

        summaryLabel.setText(issue.getSummary());
        summaryLabel.setForeground(JiraLabelUtil.BLACK);

        JiraIssueStatus status = issue.getStatus();

        statusLabel.setText(toUpperCase(status.getName()));
        statusLabel.setBackground(status.getCategoryColor());
        statusLabel.setForeground(status.isInProgressCategory() ?  IN_PROGRESS_TEXT_COLOR : JiraLabelUtil.WHITE);

        return this;
    }

}
