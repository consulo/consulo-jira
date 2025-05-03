package com.intellij.jira.actions;

import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.ex.action.DefaultActionGroup;

public class JiraIssueToolbarPopupActionGroup extends DefaultActionGroup {
    public JiraIssueToolbarPopupActionGroup() {
        super(LocalizeValue.empty(), LocalizeValue.empty(), PlatformIconGroup.actionsShow());
    }

    @Override
    public boolean isDumbAware() {
        return true;
    }
}
