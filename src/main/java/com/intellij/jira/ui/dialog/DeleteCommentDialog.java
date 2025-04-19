package com.intellij.jira.ui.dialog;

import com.intellij.jira.tasks.DeleteCommentTask;
import com.intellij.jira.ui.panels.JiraPanel;
import consulo.project.Project;
import consulo.ui.ex.awt.DialogWrapper;
import consulo.ui.ex.awt.JBLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static java.util.Objects.nonNull;

public class DeleteCommentDialog extends DialogWrapper {

    private final Project project;
    private final String issueKey;
    private final String commentId;

    public DeleteCommentDialog(Project project, String issueKey, String commentId) {
        super(project, false);
        this.project = project;
        this.issueKey = issueKey;
        this.commentId = commentId;
        init();
    }

    @Override
    protected void init() {
        setTitle(String.format("Delete comment in %s", issueKey));
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JiraPanel(new BorderLayout());
        JBLabel label = new JBLabel("You are going to delete this comment. This cannot be undone.");
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }


    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{new DeleteCommentExecuteAction(), myCancelAction};
    }


    @Override
    protected void doOKAction() {
        if(nonNull(project)){
            new DeleteCommentTask(project, issueKey, commentId).queue();
        }
        close(0);
    }

    private class DeleteCommentExecuteAction extends OkAction{

    }


}
