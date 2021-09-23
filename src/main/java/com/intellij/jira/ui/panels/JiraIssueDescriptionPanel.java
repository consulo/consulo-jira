package com.intellij.jira.ui.panels;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.actions.ChangelistActionGroup;
import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.listener.JiraIssueChangeListener;
import com.intellij.jira.listener.JiraIssuesRefreshedListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraTextPane;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static com.intellij.jira.util.JiraLabelUtil.*;
import static com.intellij.jira.util.JiraPanelUtil.MARGIN_BOTTOM;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.PAGE_START;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class JiraIssueDescriptionPanel extends AbstractJiraToolWindowPanel {

    private final Project project;
    private JiraIssue issue;

    public JiraIssueDescriptionPanel(@NotNull Project project, JiraIssue issue) {
        super(issue);
        this.project = project;
        this.issue = issue;

        init();
        subscribeTopic();
    }

    @Override
    public ActionGroup getActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new ChangelistActionGroup());

        return group;
    }

    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {
        if (JiraDataKeys.ISSUE.is(dataId)) {
            return issue;
        }

        return super.getData(dataId);
    }

    private void init() {
        setBackground(JBColor.WHITE);
        setBorder(JBUI.Borders.customLine(JBColor.border(),0, 0, 0, 1));

        JPanel issueDetails = new JiraPanel().withBackground(JBColor.WHITE).withBorder(JBUI.Borders.empty(5, 5, 1, 5));
        issueDetails.setLayout(new BoxLayout(issueDetails, Y_AXIS));

        // Summary
        if (StringUtil.isNotEmpty(issue.getSummary())) {
            JPanel issueSummaryPanel = JiraPanelUtil.createWhitePanel(new BorderLayout()).withBorder(MARGIN_BOTTOM);
            JBLabel summaryLabel = JiraLabelUtil.createLabel("Summary: ").withFont(BOLD);
            JTextArea summaryArea = new JTextArea(issue.getSummary());
            summaryArea.setLineWrap(true);
            summaryArea.setWrapStyleWord(true);
            summaryArea.setEditable(false);
            summaryArea.setBackground(UIUtil.isUnderDarcula() ? DACULA_DEFAULT_COLOR : WHITE);

            issueSummaryPanel.add(summaryLabel, PAGE_START);
            issueSummaryPanel.add(summaryArea, CENTER);

            issueDetails.add(issueSummaryPanel);
        }

        // Description
        if (StringUtil.isNotEmpty(issue.getRenderedDescription())) {
            JPanel issueDescriptionPanel = JiraPanelUtil.createWhitePanel(new BorderLayout());
            JBLabel descriptionLabel = JiraLabelUtil.createLabel("Description: ").withFont(BOLD);
            JiraTextPane descriptionTextPane = new JiraTextPane();
            descriptionTextPane.setHTMLText(issue.getRenderedDescription());

            issueDescriptionPanel.add(descriptionLabel, PAGE_START);
            issueDescriptionPanel.add(descriptionTextPane, CENTER);

            issueDetails.add(issueDescriptionPanel);
        }

        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(issueDetails, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(JBUI.Borders.empty());

        setContent(scrollPane);
    }

    private void subscribeTopic() {
        MessageBusConnection connect = project.getMessageBus().connect();

        connect.subscribe(JiraIssueChangeListener.TOPIC, issue -> {
            if (issue.getKey().equals(this.issue.getKey())) {
                this.issue = issue;
                init();
            }
        });

        connect.subscribe(JiraIssuesRefreshedListener.TOPIC, issues -> {
            int issueIndex = issues.indexOf(this.issue);
            if (issueIndex > -1) {
                this.issue = issues.get(issueIndex);
                init();
            }
        });
    }
}
