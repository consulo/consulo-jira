package com.intellij.jira.ui.panels;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.actions.AddIssueAttachmentDialogAction;
import com.intellij.jira.actions.DeleteIssueAttachmentDialogAction;
import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.actions.OpenAttachmentInBrowserAction;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueAttachment;
import com.intellij.jira.ui.model.JiraIssueAttachmentListModel;
import com.intellij.jira.ui.renders.JiraIssueAttachmentListCellRenderer;
import consulo.application.ApplicationManager;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.awt.JBList;
import consulo.ui.ex.awt.ScrollPaneFactory;
import consulo.util.dataholder.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class JiraIssueAttachmentsPanel extends AbstractJiraToolWindowPanel {

    private JiraIssueAttachment issueAttachment;
    private JBList<JiraIssueAttachment> issueAttachmentList;

    JiraIssueAttachmentsPanel(JiraIssue issue) {
        super(true, issue);
        initContent(issue.getAttachments());
    }

    @Override
    public ActionGroup getActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new AddIssueAttachmentDialogAction());
        group.add(new DeleteIssueAttachmentDialogAction());
        group.add(new OpenAttachmentInBrowserAction());

        return group;
    }

    @Override
    public @Nullable Object getData(@NotNull Key dataId) {
        if (JiraDataKeys.ISSUE_ATTACHMENT.is(dataId)
                && Objects.nonNull(issueAttachmentList.getSelectedValue())) {
            return  issueAttachment;
        }

        return super.getData(dataId);
    }

    private void initContent(List<JiraIssueAttachment> issueAttachments) {
        JPanel panel = new JiraPanel(new BorderLayout());

        issueAttachmentList = new JBList<>();
        issueAttachmentList.setEmptyText("No attachments");
        issueAttachmentList.setModel(new JiraIssueAttachmentListModel(issueAttachments));
        issueAttachmentList.setCellRenderer(new JiraIssueAttachmentListCellRenderer());
        issueAttachmentList.setSelectionMode(SINGLE_SELECTION);
        issueAttachmentList.addListSelectionListener(e -> {
            ApplicationManager.getApplication().invokeLater(this::updateToolbarActions);
        });

        panel.add(ScrollPaneFactory.createScrollPane(issueAttachmentList, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER), CENTER);

        setContent(panel);
    }

    private void updateToolbarActions() {
        JiraIssueAttachment selectedAttachment = issueAttachmentList.getSelectedValue();
        if(!Objects.equals(issueAttachment, selectedAttachment)){
            issueAttachment = selectedAttachment;
        }
    }

}
