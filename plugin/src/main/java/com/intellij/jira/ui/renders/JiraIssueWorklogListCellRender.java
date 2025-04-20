package com.intellij.jira.ui.renders;

import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.jira.ui.panels.JiraPanel;
import com.intellij.jira.util.JiraIssueUtil;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.jira.util.JiraPanelUtil;
import consulo.ui.ex.awt.JBLabel;
import consulo.ui.ex.awt.JBUI;

import javax.swing.*;
import java.awt.*;

import static com.intellij.jira.util.JiraLabelUtil.BOLD;
import static com.intellij.jira.util.JiraLabelUtil.ITALIC;

public class JiraIssueWorklogListCellRender extends DefaultJiraListCellRender {

    private JPanel worklogPanel;
    private JBLabel authorLabel;
    private JBLabel createdLabel;
    private JBLabel timeSpentLabel;

    public JiraIssueWorklogListCellRender() {
        super();
        init();
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, false, cellHasFocus);

        JiraIssueWorklog worklog = (JiraIssueWorklog) value;

        if(isSelected){
            worklogPanel.setBorder(JBUI.Borders.customLine(list.getSelectionBackground(), 0, 5, 0, 0));
        }
        else {
            worklogPanel.setBorder(JBUI.Borders.emptyLeft(5));
        }

        authorLabel.setText(worklog.getAuthor().getDisplayName());
        createdLabel.setText(JiraIssueUtil.getPrettyDateTime(worklog.getStarted()));
        timeSpentLabel.setText(worklog.getTimeSpent());

        return this;
    }

    private void init() {
        worklogPanel = new JiraPanel(new BorderLayout())
                .withBorder(JBUI.Borders.emptyLeft(5)).andTransparent();

        JPanel subPanel = new JiraPanel(new BorderLayout())
                .withBorder(JBUI.Borders.empty(4, 2, 4 , 5)).andTransparent();

        JPanel authorPanel = JiraPanelUtil.createWhitePanel(new GridLayout(1,2));
        authorLabel = JiraLabelUtil.createEmptyLabel().withFont(BOLD);
        createdLabel = JiraLabelUtil.createEmptyLabel().withFont(ITALIC);
        createdLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        authorPanel.add(authorLabel);
        authorPanel.add(createdLabel);

        subPanel.add(authorPanel, BorderLayout.PAGE_START);

        JPanel timeSpentPanel = JiraPanelUtil.createWhitePanel(new BorderLayout());
        timeSpentLabel = JiraLabelUtil.createEmptyLabel();

        timeSpentPanel.add(timeSpentLabel);

        subPanel.add(timeSpentPanel, BorderLayout.CENTER);

        worklogPanel.add(subPanel, BorderLayout.CENTER);

        add(worklogPanel);
    }

}
