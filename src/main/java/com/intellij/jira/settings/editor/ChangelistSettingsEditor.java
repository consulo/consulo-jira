package com.intellij.jira.settings.editor;

import com.intellij.jira.settings.ChangelistState;
import consulo.ui.ex.awt.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionListener;

public abstract class ChangelistSettingsEditor {

    protected final JRadioButton myRadioButton;

    public ChangelistSettingsEditor(JRadioButton myRadioButton) {
        this.myRadioButton = myRadioButton;
    }

    public abstract JPanel getPanel();

    public abstract ChangelistState getChangelistState();

    @Nullable
    public ValidationInfo validate() {
        return null;
    }

    public boolean isSelected() {
        return myRadioButton.isSelected();
    }

    public void enableComponents() {}

    public void disableComponents() {
        this.myRadioButton.setSelected(false);
    }

    public void installListener(ActionListener actionListener) {
        this.myRadioButton.addActionListener(actionListener);
    }

}
