package com.intellij.jira.actions;

import com.intellij.jira.ui.dialog.NewSearcherDialog;
import consulo.application.AllIcons;
import consulo.project.Project;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;

import static java.util.Objects.nonNull;

public class AddSearcherAction extends AnAction {

    public AddSearcherAction() {
        super("New Searcher", null, AllIcons.General.Add);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(Project.KEY);
        if(nonNull(project)){
            NewSearcherDialog dialog = new NewSearcherDialog(project);
            dialog.show();
        }

    }

}
