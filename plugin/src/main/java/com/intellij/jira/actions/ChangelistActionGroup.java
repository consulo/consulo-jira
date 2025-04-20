package com.intellij.jira.actions;

import consulo.application.AllIcons;
import consulo.ui.ex.action.AnSeparator;
import consulo.ui.ex.action.DefaultActionGroup;

public class ChangelistActionGroup extends DefaultActionGroup {

    public ChangelistActionGroup() {
        super("Changelist", true);
        getTemplatePresentation().setIcon(AllIcons.Vcs.Changelist);
        add(new AddIssueChangeListAction());
        add(new AnSeparator());
        add(new ChangelistSettingsDialogAction());
    }

}
