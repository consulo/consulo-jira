package com.intellij.jira.actions;

import consulo.application.dumb.DumbAware;
import consulo.ui.ex.action.AnAction;
import jakarta.annotation.Nonnull;

import javax.swing.*;

public abstract class JiraIssueAction extends AnAction implements DumbAware {

    private ActionProperties actionProperties;
    private JComponent component;

    public JiraIssueAction(@Nonnull ActionProperties actionProperties) {
        super(actionProperties.getText(), actionProperties.getDescription(), actionProperties.getIcon());
        this.actionProperties = actionProperties;
    }

    public void registerComponent(JComponent component){
        this.component = component;
        registerCustomShortcutSet(actionProperties.getShortcut(), component);
    }

    public JComponent getComponent() {
        return component;
    }
}
