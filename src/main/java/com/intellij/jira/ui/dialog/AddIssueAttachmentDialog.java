package com.intellij.jira.ui.dialog;

import com.intellij.jira.tasks.AddIssueAttachmentTask;
import com.intellij.jira.ui.editors.FileChooserFieldEditor;
import consulo.project.Project;
import consulo.ui.ex.awt.DialogWrapper;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.ValidationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static java.util.Objects.nonNull;

public class AddIssueAttachmentDialog extends DialogWrapper {

    private FileChooserFieldEditor myFileChooserFieldEditor;
    private Project myProject;
    private String issueKey;

    public AddIssueAttachmentDialog(@NotNull Project project, @NotNull String issueKey) {
        super(project, false);
        myProject = project;
        this.issueKey = issueKey;

        setTitle(String.format("Add Attachment to %s", issueKey));
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        myFileChooserFieldEditor = new FileChooserFieldEditor();

        return FormBuilder.createFormBuilder()
                .addComponent(myFileChooserFieldEditor.createPanel())
                .addVerticalGap(5)
                .getPanel();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return myFileChooserFieldEditor.validate();
    }

    @Override
    protected void doOKAction() {
        if(nonNull(myProject)){
            new AddIssueAttachmentTask(myProject, issueKey, myFileChooserFieldEditor.getSelectedFile()).queue();
        }

        close(0);
    }
}
