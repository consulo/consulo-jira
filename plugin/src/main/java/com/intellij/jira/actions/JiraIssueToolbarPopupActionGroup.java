package com.intellij.jira.actions;

import consulo.ui.ex.action.DefaultActionGroup;

public class JiraIssueToolbarPopupActionGroup extends DefaultActionGroup {

    @Override
    public boolean isDumbAware() {
        return true;
    }
}
