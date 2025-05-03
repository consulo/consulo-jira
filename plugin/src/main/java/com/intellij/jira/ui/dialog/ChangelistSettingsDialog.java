package com.intellij.jira.ui.dialog;

import com.intellij.jira.settings.ChangelistSettings;
import com.intellij.jira.settings.ChangelistState;
import com.intellij.jira.settings.editor.ChangelistSettingsEditor;
import com.intellij.jira.settings.editor.CustomChangelistSettingsEditor;
import com.intellij.jira.settings.editor.DefaultChangelistSettingsEditor;
import consulo.project.Project;
import consulo.ui.ex.awt.DialogWrapper;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.ValidationInfo;
import jakarta.annotation.Nullable;

import javax.swing.*;

public class ChangelistSettingsDialog extends DialogWrapper {

    private ChangelistSettingsEditor defaultChangelistSettingsEditor;
    private ChangelistSettingsEditor customChangelistSettingsEditor;
    private final ChangelistSettings settings;

    public ChangelistSettingsDialog(@Nullable Project project) {
        super(project, false);
        settings = ChangelistSettings.getInstance();

        setTitle("Changelist Settings");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        this.defaultChangelistSettingsEditor = new DefaultChangelistSettingsEditor(settings);
        this.defaultChangelistSettingsEditor.installListener(actionEvent -> {
            if (this.defaultChangelistSettingsEditor.isSelected()) {
                this.defaultChangelistSettingsEditor.enableComponents();
                this.customChangelistSettingsEditor.disableComponents();
            } else {
                this.defaultChangelistSettingsEditor.disableComponents();
                this.customChangelistSettingsEditor.enableComponents();
            }
        });

        this.customChangelistSettingsEditor = new CustomChangelistSettingsEditor(settings);
        this.customChangelistSettingsEditor.installListener(actionEvent -> {
            if (this.customChangelistSettingsEditor.isSelected()) {
                this.customChangelistSettingsEditor.enableComponents();
                this.defaultChangelistSettingsEditor.disableComponents();
            } else {
                this.customChangelistSettingsEditor.disableComponents();
                this.defaultChangelistSettingsEditor.enableComponents();
            }
        });


        return FormBuilder.createFormBuilder()
                .addComponent(defaultChangelistSettingsEditor.getPanel())
                .addComponent(customChangelistSettingsEditor.getPanel())
                .getPanel();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (defaultChangelistSettingsEditor.isSelected()) {
            return defaultChangelistSettingsEditor.validate();
        }

        return customChangelistSettingsEditor.validate();
    }

    @Override
    protected void doOKAction() {
        ChangelistState changelistState;
        if (defaultChangelistSettingsEditor.isSelected()) {
            changelistState = defaultChangelistSettingsEditor.getChangelistState();
        } else {
            changelistState = customChangelistSettingsEditor.getChangelistState();
        }

        ChangelistSettings settings = ChangelistSettings.getInstance();
        settings.loadState(changelistState);

        super.doOKAction();
    }
}
