package com.intellij.jira.actions;

import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.ex.action.AnSeparator;
import consulo.ui.ex.action.DefaultActionGroup;

public class ChangelistActionGroup extends DefaultActionGroup {

    public ChangelistActionGroup() {
        super("Changelist", true);
        getTemplatePresentation().setIcon(PlatformIconGroup.scopeChangedfiles());
        add(new AddIssueChangeListAction());
        add(new AnSeparator());
        add(new ChangelistSettingsDialogAction());
    }

}
