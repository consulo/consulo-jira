package com.intellij.jira.ui.panels;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.actions.BranchActionGroup;
import com.intellij.jira.actions.ChangelistActionGroup;
import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.actions.OpenIssueTaskAction;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.listener.IssueChangeListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraTextPane;
import com.intellij.jira.util.JiraBorders;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.jira.util.JiraPanelUtil;
import consulo.ui.ex.JBColor;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.awt.JBLabel;
import consulo.ui.ex.awt.JBUI;
import consulo.ui.ex.awt.ScrollPaneFactory;
import consulo.ui.ex.awt.UIUtil;
import consulo.util.dataholder.Key;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.NonNls;
import jakarta.annotation.Nullable;

import javax.swing.*;

import static com.intellij.jira.util.JiraLabelUtil.DACULA_DEFAULT_COLOR;
import static com.intellij.jira.util.JiraLabelUtil.WHITE;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.PAGE_START;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class JiraIssueDescriptionPanel extends AbstractJiraToolWindowPanel {

    private final JiraIssuesData myIssuesData;
    private JiraIssue myIssue;

    public JiraIssueDescriptionPanel(@Nonnull JiraIssuesData issuesData, @Nonnull JiraIssue issue) {
        super(issue);
        myIssuesData = issuesData;
        myIssue = issue;

        init();
        subscribeTopic();
    }

    @Override
    public ActionGroup getActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new ChangelistActionGroup());
        group.add(new BranchActionGroup());
        group.add(new OpenIssueTaskAction());

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

        JPanel issueDetails = new JiraScrollablePanel();

        // Summary
        if (StringUtil.isNotEmpty(myIssue.getSummary())) {
            JPanel issueSummaryPanel = JiraPanelUtil.createWhiteBorderPanel().withBorder(JiraBorders.emptyBottom(15));
            JBLabel summaryLabel = JiraLabelUtil.createBoldLabel("Summary: ");
            JTextArea summaryArea = new JTextArea(myIssue.getSummary());
            summaryArea.setLineWrap(true);
            summaryArea.setWrapStyleWord(true);
            summaryArea.setEditable(false);
            summaryArea.setBackground(UIUtil.isUnderDarcula() ? DACULA_DEFAULT_COLOR : WHITE);

            issueSummaryPanel.add(summaryLabel, PAGE_START);
            issueSummaryPanel.add(summaryArea, CENTER);

            issueDetails.add(issueSummaryPanel);
        }

        // Description
        if (StringUtil.isNotEmpty(myIssue.getRenderedDescription())) {
            JPanel issueDescriptionPanel = JiraPanelUtil.createWhiteBorderPanel();
            JBLabel descriptionLabel = JiraLabelUtil.createBoldLabel("Description: ");
            JiraTextPane descriptionTextPane = new JiraTextPane();
            descriptionTextPane.setHTMLText(myIssue.getRenderedDescription());

            issueDescriptionPanel.add(descriptionLabel, PAGE_START);
            issueDescriptionPanel.add(descriptionTextPane, CENTER);

            issueDetails.add(issueDescriptionPanel);
        }

        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(issueDetails, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(JBUI.Borders.empty());

        setContent(scrollPane);
    }

    public void update(@Nonnull JiraIssue issue) {
        myIssue = issue;
        init();
    }

    private void subscribeTopic() {
        myIssuesData.getProject().getMessageBus().connect().subscribe(IssueChangeListener.TOPIC, issue -> {
            if (issue.getKey().equals(myIssue.getKey())) {
                myIssue = issue;
                init();
            }
        });

    }
}
