package com.intellij.jira.ui.dialog;

import com.intellij.jira.settings.branch.BranchSettings;
import com.intellij.jira.settings.branch.editor.BranchSettingsEditor;
import consulo.project.Project;
import consulo.ui.ex.awt.DialogWrapper;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.ValidationInfo;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.swing.*;

public class BranchSettingsDialog extends DialogWrapper {

    private final BranchSettings myBranchSettings;

    private final BranchSettingsEditor myBranchSettingsEditor;

    public BranchSettingsDialog(@Nonnull Project project, @Nonnull BranchSettings branchSettings) {
        super(project, false);
        myBranchSettings = branchSettings;
        myBranchSettingsEditor = new BranchSettingsEditor(project, branchSettings);

        setTitle("Branch Settings");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return FormBuilder.createFormBuilder()
                .addComponent(myBranchSettingsEditor.createPanel())
                .getPanel();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return myBranchSettingsEditor.validate();
    }

    @Override
    protected void doOKAction() {
        myBranchSettings.loadState(myBranchSettingsEditor.getBranchSettingsState());

        super.doOKAction();
    }
}
