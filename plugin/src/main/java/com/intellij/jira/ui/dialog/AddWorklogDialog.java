package com.intellij.jira.ui.dialog;

import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.jira.tasks.AddWorklogTask;
import consulo.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

import static java.util.Objects.nonNull;

public class AddWorklogDialog extends EditWorklogDialog {

    public AddWorklogDialog(@Nullable Project project, String issueKey, List<String> projectRoles, JiraIssueTimeTracking timeTracking) {
        super(project, issueKey, projectRoles, new JiraIssueWorklog(), timeTracking,true);
        setTitle("Add Log Work: " + issueKey);
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{new AddWorklogExecuteAction(), myCancelAction};
    }

    @Override
    protected void doOKAction() {
        if(nonNull(myProject)){
            new AddWorklogTask(myProject, myIssueKey, myWorklogFields, myRemainingEstimateEditor.getJsonValue()).queue();
        }

        close(0);
    }

    private class AddWorklogExecuteAction extends OkAction{ }

}
