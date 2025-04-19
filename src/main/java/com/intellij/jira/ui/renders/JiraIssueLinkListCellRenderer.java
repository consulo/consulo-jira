package com.intellij.jira.ui.renders;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueLink;
import com.intellij.jira.rest.model.JiraIssueStatus;
import com.intellij.jira.util.JiraBorders;
import com.intellij.jira.util.JiraLabelUtil;
import consulo.ui.ex.JBColor;
import consulo.ui.ex.awt.BorderLayoutPanel;
import consulo.ui.ex.awt.JBLabel;
import consulo.ui.ex.awt.JBUI;
import consulo.ui.ex.awt.UIUtil;

import javax.swing.*;
import java.awt.*;

import static com.intellij.jira.util.JiraLabelUtil.*;
import static consulo.util.lang.StringUtil.toUpperCase;
import static java.util.Objects.nonNull;

public class JiraIssueLinkListCellRenderer extends DefaultJiraListCellRender  {

    private JBLabel typeLabel;
    private JBLabel issueKeyAndSummaryLabel;
    private JBLabel issueStatusLabel;

    public JiraIssueLinkListCellRenderer() {
        super();
        init();
    }

    private void init() {
        BorderLayoutPanel issueLinkPanel = new BorderLayoutPanel()
                .withBorder(JiraBorders.empty(4, 5)).andTransparent();
        typeLabel =  JiraLabelUtil.createEmptyLabel().withFont(BOLD);
        issueStatusLabel = JiraLabelUtil.createEmptyStatusLabel();
        issueKeyAndSummaryLabel = JiraLabelUtil.createEmptyLabel().withBorder(JBUI.Borders.emptyLeft(10));
        issueLinkPanel.add(typeLabel, BorderLayout.LINE_START);
        issueLinkPanel.addToCenter(issueKeyAndSummaryLabel);
        issueLinkPanel.add(issueStatusLabel, BorderLayout.LINE_END);
        add(issueLinkPanel);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        JiraIssueLink issueLink = (JiraIssueLink) value;

        setBorder(JiraBorders.emptyBottom(2));

        if (isSelected) {
            setBackground(UIUtil.isUnderDarcula() ? DARK_DARCULA_ISSUE_LINK_COLOR : DARK_ISSUE_LINK_COLOR);
        } else {
            setBackground(UIUtil.isUnderDarcula() ? DARCULA_ISSUE_LINK_COLOR : ISSUE_LINK_COLOR);
        }

        String typeText = nonNull(issueLink.getInwardIssue()) ? issueLink.getType().getInward() : issueLink.getType().getOutward();
        typeLabel.setText(typeText);
        typeLabel.setForeground(getFgRowColor());

        JiraIssue issue = nonNull(issueLink.getInwardIssue()) ? issueLink.getInwardIssue() : issueLink.getOutwardIssue();
        JiraIssueStatus status = issue.getStatus();

        String issueKeyAndSummaryText = getIssueKeyAndSummary(issue);
        issueKeyAndSummaryLabel.setText(issueKeyAndSummaryText);
        issueKeyAndSummaryLabel.setForeground(issue.isResolved() ? JBColor.border() : list.getForeground());


        issueStatusLabel.setText(toUpperCase(status.getName()));
        issueStatusLabel.setBackground(status.getCategoryColor());
        issueStatusLabel.setForeground(status.isInProgressCategory() ?  IN_PROGRESS_TEXT_COLOR : Color.WHITE);

        return this;
    }

    private String getIssueKeyAndSummary(JiraIssue issue){
        return issue.getKey() + " " + issue.getSummary();
    }

}
