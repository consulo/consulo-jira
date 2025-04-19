package com.intellij.jira.ui.panels;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.actions.BranchActionGroup;
import com.intellij.jira.actions.ChangelistActionGroup;
import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.actions.JiraIssueAssigneePopupAction;
import com.intellij.jira.actions.JiraIssuePrioritiesPopupAction;
import com.intellij.jira.actions.OpenIssueTaskAction;
import com.intellij.jira.actions.OpenNewJiraTabAction;
import com.intellij.jira.actions.TransitIssueDialogAction;
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
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import static com.intellij.jira.util.JiraLabelUtil.DACULA_DEFAULT_COLOR;
import static com.intellij.jira.util.JiraLabelUtil.WHITE;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.PAGE_START;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

class JiraIssuePreviewPanel extends AbstractJiraToolWindowPanel {

    private final Project myProject;
    private JiraIssue issue;

    JiraIssuePreviewPanel(@NotNull Project project, @NotNull JiraIssue issue) {
        super(true, issue);
        this.myProject = project;
        this.issue = issue;
        setBackground(JBColor.white);

        initContent();
    }

    @Override
    public ActionGroup getActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new TransitIssueDialogAction());
        group.add(new JiraIssueAssigneePopupAction());
        group.add(new JiraIssuePrioritiesPopupAction());
        group.add(new ChangelistActionGroup());
        group.add(new BranchActionGroup());
        group.add(new OpenIssueTaskAction());
        group.addSeparator();
        group.add(new OpenNewJiraTabAction());

        return group;
    }

    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {
        if (JiraDataKeys.ISSUE.is(dataId)) {
            return issue;
        }

        return super.getData(dataId);
    }

    private void initContent() {

        JPanel issueDetails = new JiraScrollablePanel();

        // Key and status
        JBLabel projectKeyLabel = JiraLabelUtil.createLinkLabel(issue.getProject().getName(), issue.getProject().getUrl());
        JBLabel separatorLabel = JiraLabelUtil.createLabel(" / ");
        JBLabel issueKeyLabel = JiraLabelUtil.createLinkLabel(issue.getKey(), issue.getUrl());
        JBLabel statusLabel = JiraLabelUtil.createStatusLabel(issue.getStatus());

        JPanel keyAndStatusPanel = JiraPanelUtil.createWhiteLeftFlowPanel(projectKeyLabel,
                                                                            separatorLabel,
                                                                            issueKeyLabel,
                                                                            JiraLabelUtil.space(),
                                                                            statusLabel);

        issueDetails.add(keyAndStatusPanel);

        // Summary
        if (StringUtil.isNotEmpty(issue.getSummary())) {
            JPanel issueSummaryPanel = JiraPanelUtil.createWhiteBorderPanel();
            JTextArea summaryArea = new JTextArea(issue.getSummary());
            summaryArea.setLineWrap(true);
            summaryArea.setWrapStyleWord(true);
            summaryArea.setEditable(false);
            summaryArea.setBackground(UIUtil.isUnderDarcula() ? DACULA_DEFAULT_COLOR : WHITE);

            issueSummaryPanel.add(summaryArea, CENTER);

            issueDetails.add(issueSummaryPanel);
        }

        // Type and reporter
        JPanel typeAndReporterPanel = JiraPanelUtil.createWhiteGridPanel(1, 2);
        JPanel typePanel = JiraPanelUtil.createTypePanel(issue);
        JPanel reporterPanel = JiraPanelUtil.createReporterPanel(issue);

        typeAndReporterPanel.add(typePanel);
        typeAndReporterPanel.add(reporterPanel);

        issueDetails.add(typeAndReporterPanel);

        // Priority and Assignee
        JPanel priorityAndAssigneePanel = JiraPanelUtil.createWhiteGridPanel(1, 2);
        JPanel priorityPanel = JiraPanelUtil.createPriorityPanel(issue);
        JPanel assigneePanel = JiraPanelUtil.createAssigneePanel(issue);

        priorityAndAssigneePanel.add(priorityPanel);
        priorityAndAssigneePanel.add(assigneePanel);

        issueDetails.add(priorityAndAssigneePanel);

        // Watches
        JPanel watchesPanel = JiraPanelUtil.createWatchesPanel(issue, myProject);
        issueDetails.add(watchesPanel);

        // Versions
        JPanel versionsPanel = JiraPanelUtil.createVersionsPanel(issue);
        issueDetails.add(versionsPanel);

        // Components
        if(issue.hasComponents()){
            JPanel componentsPanel = JiraPanelUtil.createComponentsPanel(issue);
            issueDetails.add(componentsPanel);
        }

        // Labels
        if(issue.hasLabels()){
            JPanel labelsPanel = JiraPanelUtil.createLabelsPanel(issue);
            issueDetails.add(labelsPanel);
        }

        // Description
        if (StringUtil.isNotEmpty(issue.getRenderedDescription())) {
            JPanel issueDescriptionPanel = JiraPanelUtil.createWhiteBorderPanel();
            JBLabel descriptionLabel = JiraLabelUtil.createBoldLabel("Description: ");
            JiraTextPane descriptionTextPane = new JiraTextPane();
            descriptionTextPane.setHTMLText(issue.getRenderedDescription());

            issueDescriptionPanel.add(descriptionLabel, PAGE_START);
            issueDescriptionPanel.add(descriptionTextPane, CENTER);

            issueDetails.add(issueDescriptionPanel);
        }

        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(issueDetails, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);

        setContent(scrollPane);
    }

}
