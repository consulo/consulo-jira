package com.intellij.jira.settings.editor;

import com.intellij.jira.settings.ChangelistSettings;
import com.intellij.jira.settings.ChangelistState;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.JBRadioButton;

import javax.swing.*;

public class DefaultChangelistSettingsEditor extends ChangelistSettingsEditor {

    private static final JRadioButton DEFAULT_RADIO_BUTTON = new JBRadioButton("Creates a changelist using issue key as name");

    public DefaultChangelistSettingsEditor(ChangelistSettings settings) {
        super(DEFAULT_RADIO_BUTTON);
        init(settings);
    }

    private void init(ChangelistSettings settings) {
        this.myRadioButton.setSelected(settings.isDefault());

        if (settings.isCustom()) {
            disableComponents();
        }
    }

    @Override
    public JPanel getPanel() {
        return FormBuilder.createFormBuilder()
                .addComponent(myRadioButton)
                .getPanel();
    }

    @Override
    public ChangelistState getChangelistState() {
        return ChangelistState.getDefault();
    }

}
