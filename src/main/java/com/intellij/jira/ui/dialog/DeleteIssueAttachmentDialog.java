package com.intellij.jira.ui.dialog;

import com.intellij.jira.tasks.DeleteIssueAttachmentTask;
import consulo.project.Project;
import consulo.ui.ex.awt.DialogWrapper;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.JBLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static java.util.Objects.nonNull;

public class DeleteIssueAttachmentDialog extends DialogWrapper {

    private Project project;
    private String issueKey;
    private String attachmentId;

    public DeleteIssueAttachmentDialog(@NotNull Project project, String issueKey, String attachmentId) {
        super(project, false);
        this.project = project;
        this.issueKey = issueKey;
        this.attachmentId = attachmentId;

        setTitle(String.format("Delete Attachment in %s", issueKey));
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return FormBuilder.createFormBuilder()
                .addComponent(new JBLabel("You are going to delete this attachment. This cannot be undone."))
                .getPanel();
    }

    @Override
    protected void doOKAction() {
        if(nonNull(project)){
            new DeleteIssueAttachmentTask(project, issueKey, attachmentId).queue();
        }

        close(0);
    }

}
