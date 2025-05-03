package com.intellij.jira.ui.panels;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.actions.AddCommentDialogAction;
import com.intellij.jira.actions.DeleteCommentDialogAction;
import com.intellij.jira.actions.EditCommentDialogAction;
import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.rest.JiraIssueCommentsWrapper;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.ui.model.JiraIssueCommentListModel;
import com.intellij.jira.ui.renders.JiraIssueCommentListCellRenderer;
import consulo.application.ApplicationManager;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.awt.JBList;
import consulo.ui.ex.awt.JBUI;
import consulo.ui.ex.awt.ScrollPaneFactory;
import consulo.util.dataholder.Key;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

class JiraIssueCommentsPanel extends AbstractJiraToolWindowPanel {

    private JiraIssueComment comment;
    private JBList<JiraIssueComment> issueCommentList;

    JiraIssueCommentsPanel(@Nonnull JiraIssue issue) {
        super(issue);
        initContent(issue.getRenderedComments());
    }

    @Override
    public ActionGroup getActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new AddCommentDialogAction());
        group.add(new EditCommentDialogAction());
        group.add(new DeleteCommentDialogAction());

        return group;
    }

    @Override
    public @Nullable Object getData(@Nonnull Key dataId) {
        if (JiraDataKeys.ISSUE_COMMENT.is(dataId) && Objects.nonNull(issueCommentList.getSelectedValue())) {
            return comment;
        }

        return super.getData(dataId);
    }

    private void initContent(JiraIssueCommentsWrapper comments){
        JPanel panel = new JiraPanel(new BorderLayout());

        issueCommentList = new JBList<>();
        issueCommentList.setEmptyText("No comments");
        issueCommentList.setModel(new JiraIssueCommentListModel(comments.getComments()));
        issueCommentList.setCellRenderer(new JiraIssueCommentListCellRenderer());
        issueCommentList.setSelectionMode(SINGLE_SELECTION);
        issueCommentList.addListSelectionListener(e -> {
            ApplicationManager.getApplication().invokeLater(this::updateToolbarActions);
        });

        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(issueCommentList, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(JBUI.Borders.empty());

        panel.add(scrollPane, CENTER);

        setContent(panel);
    }

    private void updateToolbarActions() {
        JiraIssueComment selectedComment = issueCommentList.getSelectedValue();
        if(!Objects.equals(comment, selectedComment)){
            comment = selectedComment;
        }
    }

}
