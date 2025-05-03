package com.intellij.jira.ui.panels;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.listener.IssueChangeListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraTabbedPane;
import consulo.application.ApplicationManager;
import consulo.component.messagebus.MessageBusConnection;
import jakarta.annotation.Nonnull;

import javax.swing.*;

public class JiraIssueActivityPanel extends JiraTabbedPane {

    private final JiraIssuesData myIssuesData;
    private JiraIssue myIssue;

    private int mySelectedTab = 0;

    JiraIssueActivityPanel(@Nonnull JiraIssuesData issuesData, JiraIssue issue) {
        super(JTabbedPane.BOTTOM);
        myIssuesData = issuesData;
        myIssue = issue;

        addTabs();
        setSelectedIndex(mySelectedTab);
        addChangeListener(e -> mySelectedTab = getSelectedIndex());
        subscribeTopic();
    }

    public void update(@Nonnull JiraIssue issue) {
        myIssue = issue;
        updatePanel();
    }

    private String appendTotal(int total) {
        return total > 0 ? " (" + total + ") " : " ";
    }

    private void addTabs() {
        addTab(JiraIssueDetailsPanel.TAB_ATTACHMENTS + appendTotal(myIssue.getAttachments().size()), new JiraIssueAttachmentsPanel(myIssue));
        addTab(JiraIssueDetailsPanel.TAB_LINKS + appendTotal(myIssue.getIssueLinks().size()), new JiraIssueLinksPanel(myIssue));
        addTab(JiraIssueDetailsPanel.TAB_SUB_TASKS + appendTotal(myIssue.getSubtasks().size()), new JiraIssueSubtasksPanel(myIssue));
        addTab(JiraIssueDetailsPanel.TAB_COMMENTS + appendTotal(myIssue.getRenderedComments().getTotal()), new JiraIssueCommentsPanel(myIssue));
        addTab(JiraIssueDetailsPanel.TAB_WORK_LOG + appendTotal(myIssue.getWorklogs().size()), new JiraIssueWorkLogsPanel(myIssue));
    }

    private void subscribeTopic() {
        MessageBusConnection connect = myIssuesData.getProject().getMessageBus().connect();
        connect.subscribe(IssueChangeListener.TOPIC, issue -> {
            if (issue.getKey().equals(myIssue.getKey())) {
                myIssue = issue;
                updatePanel();
            }
        });

    }

    private void updatePanel() {
        ApplicationManager.getApplication().invokeLater(() -> {
            int oldSelectedTab = mySelectedTab;
            while (getTabCount() > 0) {
                remove(0);
            }

            if (myIssue != null) {
                addTabs();
                setSelectedIndex(oldSelectedTab);
                mySelectedTab = getSelectedIndex();
            }
        });
    }

}
