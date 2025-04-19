package com.intellij.jira.ui.panels;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.model.JiraIssueSubtaskListModel;
import com.intellij.jira.ui.renders.JiraIssueSubtaskListCellRender;
import consulo.ui.ex.awt.JBList;
import consulo.ui.ex.awt.ScrollPaneFactory;
import consulo.ui.ex.awt.SimpleToolWindowPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class JiraIssueSubtasksPanel extends SimpleToolWindowPanel {

    public JiraIssueSubtasksPanel(JiraIssue issue) {
        super(true, true);
        initContent(issue.getSubtasks());
    }

    private void initContent(List<JiraIssue> issueSubtasks) {
        JPanel panel = new JiraPanel(new BorderLayout());

        JBList<JiraIssue> subtasks = new JBList<>();
        subtasks.setEmptyText("No sub-tasks");
        subtasks.setModel(new JiraIssueSubtaskListModel(issueSubtasks));
        subtasks.setCellRenderer(new JiraIssueSubtaskListCellRender());

        panel.add(ScrollPaneFactory.createScrollPane(subtasks, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER), CENTER);

        setContent(panel);
    }

}
