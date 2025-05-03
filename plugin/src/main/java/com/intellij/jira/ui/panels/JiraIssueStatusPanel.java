package com.intellij.jira.ui.panels;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.actions.JiraIssueAssigneePopupAction;
import com.intellij.jira.actions.JiraIssuePrioritiesPopupAction;
import com.intellij.jira.actions.TransitIssueDialogAction;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.listener.IssueChangeListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.util.JiraPanelUtil;
import consulo.component.messagebus.MessageBusConnection;
import consulo.ui.ex.JBColor;
import consulo.ui.ex.action.ActionGroup;
import consulo.util.dataholder.Key;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;

public class JiraIssueStatusPanel extends AbstractJiraToolWindowPanel {

    private final JiraIssuesData myIssuesData;
    private JiraIssue myIssue;

    public JiraIssueStatusPanel(@Nonnull JiraIssuesData issuesData, JiraIssue issue) {
        super(issue);
        myIssuesData = issuesData;
        this.myIssue = issue;

        init();
        subscribeTopic();
    }

    @Override
    public ActionGroup getActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new TransitIssueDialogAction());
        group.add(new JiraIssueAssigneePopupAction());
        group.add(new JiraIssuePrioritiesPopupAction());

        return group;
    }

    @Override
    public @Nullable Object getData(@Nonnull @NonNls Key dataId) {
        if (JiraDataKeys.ISSUE.is(dataId)) {
            return myIssue;
        }

        return super.getData(dataId);
    }

    private void init() {
        setBackground(JBColor.WHITE);

        JPanel mainPanel = new JiraScrollablePanel();

        // Status
        JPanel statusPanel = JiraPanelUtil.createStatusPanel(myIssue);
        mainPanel.add(statusPanel);

        // Priority
        JPanel priorityPanel = JiraPanelUtil.createPriorityPanel(myIssue);
        mainPanel.add(priorityPanel);

        // Reporter
        JPanel reporterPanel = JiraPanelUtil.createReporterPanel(myIssue);
        mainPanel.add(reporterPanel);

        // Assignee
        JPanel assigneePanel = JiraPanelUtil.createAssigneePanel(myIssue);
        mainPanel.add(assigneePanel);

        // Watches
        JPanel watchesPanel = JiraPanelUtil.createWatchesPanel(myIssue, myIssuesData.getProject());
        mainPanel.add(watchesPanel);

        // Versions
        JPanel versionsPanel = JiraPanelUtil.createVersionsPanel(myIssue);
        mainPanel.add(versionsPanel);

        setContent(mainPanel);
    }

    public void update(@Nonnull JiraIssue issue) {
        myIssue = issue;
        init();
    }

    private void subscribeTopic() {
        MessageBusConnection connect = myIssuesData.getProject().getMessageBus().connect();

        connect.subscribe(IssueChangeListener.TOPIC, issue -> {
            if (issue.getKey().equals(myIssue.getKey())) {
                myIssue = issue;
                init();
            }
        });

    }

}
