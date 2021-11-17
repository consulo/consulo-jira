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
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.util.Objects;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

class JiraIssueCommentsPanel extends AbstractJiraToolWindowPanel {

    private JiraIssueComment comment;
    private JBList<JiraIssueComment> issueCommentList;

    JiraIssueCommentsPanel(@NotNull JiraIssue issue) {
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
    public @Nullable Object getData(@NotNull String dataId) {
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
