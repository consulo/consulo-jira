package com.intellij.jira.ui.panels;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.actions.AddIssueLinkDialogAction;
import com.intellij.jira.actions.DeleteIssueLinkDialogAction;
import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueLink;
import com.intellij.jira.ui.model.JiraIssueLinkListModel;
import com.intellij.jira.ui.renders.JiraIssueLinkListCellRenderer;
import consulo.application.ApplicationManager;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.awt.JBList;
import consulo.ui.ex.awt.ScrollPaneFactory;
import consulo.util.dataholder.Key;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

class JiraIssueLinksPanel extends AbstractJiraToolWindowPanel {

    private JiraIssueLink issueLink;
    private JBList<JiraIssueLink> issueLinkList;

    JiraIssueLinksPanel(@Nonnull JiraIssue issue) {
        super(true, issue);
        initContent(issue.getIssueLinks());
    }

    @Override
    public ActionGroup getActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new AddIssueLinkDialogAction());
        group.add(new DeleteIssueLinkDialogAction());

        return group;
    }

    @Override
    public @Nullable Object getData(@Nonnull Key dataId) {
        if (JiraDataKeys.ISSUE_LINK.is(dataId)
            && Objects.nonNull(issueLinkList.getSelectedValue())) {
            return issueLink;
        }

        return super.getData(dataId);
    }

    private void initContent(List<JiraIssueLink> issueLinks) {
        JPanel panel = new JiraPanel(new BorderLayout());

        issueLinkList = new JBList<>();
        issueLinkList.setEmptyText("No links");
        issueLinkList.setModel(new JiraIssueLinkListModel(issueLinks));
        issueLinkList.setCellRenderer(new JiraIssueLinkListCellRenderer());
        issueLinkList.setSelectionMode(SINGLE_SELECTION);
        issueLinkList.addListSelectionListener(e -> {
            ApplicationManager.getApplication().invokeLater(this::updateToolbarActions);
        });

        panel.add(ScrollPaneFactory.createScrollPane(issueLinkList, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER), CENTER);

        setContent(panel);
    }

    private void updateToolbarActions() {
        JiraIssueLink selectedLink = issueLinkList.getSelectedValue();
        if(!Objects.equals(issueLink, selectedLink)){
            issueLink = selectedLink;
        }
    }

}
