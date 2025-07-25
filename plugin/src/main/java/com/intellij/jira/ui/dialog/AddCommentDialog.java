package com.intellij.jira.ui.dialog;

import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.tasks.AddCommentTask;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

import javax.swing.*;
import java.util.List;

import static com.intellij.jira.util.JiraIssueUtil.escapeComment;
import static java.util.Objects.nonNull;

public class AddCommentDialog extends EditCommentDialog {

    public AddCommentDialog(@Nonnull Project project, @Nonnull String issueKey, List<String> projectRoles) {
        super(project, issueKey, projectRoles, new JiraIssueComment());
        setTitle(String.format("Add a comment to %s", issueKey));
    }

    @Nonnull
    @Override
    protected Action[] createActions() {
        return new Action[]{new AddCommentExecuteAction(), myCancelAction};
    }

    @Override
    protected void doOKAction() {
        if(nonNull(myProject)){
            new AddCommentTask(myProject, issueKey, escapeComment(commentArea.getText()), (String) myComboBox.getSelectedItem()).queue();
        }

        close(0);
    }

    private class AddCommentExecuteAction extends OkAction{ }

}
