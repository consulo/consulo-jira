package com.intellij.jira.actions;

import consulo.application.dumb.DumbAware;
import consulo.ui.ex.action.AnAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class JiraIssueAction extends AnAction implements DumbAware {

    private ActionProperties actionProperties;
    private JComponent component;

    public JiraIssueAction(@NotNull ActionProperties actionProperties) {
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
