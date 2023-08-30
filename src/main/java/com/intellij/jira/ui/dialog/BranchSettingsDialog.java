package com.intellij.jira.ui.dialog;

import com.intellij.jira.settings.branch.BranchSettings;
import com.intellij.jira.settings.branch.editor.BranchSettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public class BranchSettingsDialog extends DialogWrapper {

    private final BranchSettings myBranchSettings;

    private final BranchSettingsEditor myBranchSettingsEditor;

    public BranchSettingsDialog(@NotNull Project project, @NotNull BranchSettings branchSettings) {
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
