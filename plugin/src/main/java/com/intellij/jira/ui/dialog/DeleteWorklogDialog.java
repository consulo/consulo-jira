package com.intellij.jira.ui.dialog;

import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import com.intellij.jira.tasks.DeleteWorklogTask;
import com.intellij.jira.ui.editors.RemainingEstimateFieldEditor;
import consulo.project.Project;
import consulo.ui.ex.awt.DialogWrapper;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.JBLabel;
import consulo.ui.ex.awt.ValidationInfo;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.swing.*;

import static java.util.Objects.nonNull;

public class DeleteWorklogDialog extends DialogWrapper {

    private Project project;
    private String issueKey;
    private String worklogId;

    private RemainingEstimateFieldEditor remainingEstimateFieldEditor;
    private JiraIssueTimeTracking timeTracking;

    public DeleteWorklogDialog(Project project, String issueKey, String worklogId, JiraIssueTimeTracking timeTracking) {
        super(project, false);
        this.project = project;
        this.issueKey = issueKey;
        this.worklogId = worklogId;
        this.timeTracking = timeTracking;
        init();
    }

    @Override
    protected void init() {
        setTitle(String.format("Delete Work Log: %s", issueKey));
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        this.remainingEstimateFieldEditor = new RemainingEstimateFieldEditor("Remaining Estimate", false, this.timeTracking, true);

        return FormBuilder.createFormBuilder()
                .addComponent(new JBLabel("You are going to delete this work log. This cannot be undone."))
                .addComponent(remainingEstimateFieldEditor.createPanel())
                .getPanel();
    }

    @Nonnull
    @Override
    protected Action[] createActions() {
        return new Action[]{new DeleteWorklogExecuteAction(), myCancelAction};
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return remainingEstimateFieldEditor.validate();
    }

    @Override
    protected void doOKAction() {
        if(nonNull(project)){
            new DeleteWorklogTask(project, issueKey, worklogId, remainingEstimateFieldEditor.getJsonValue()).queue();
        }

        close(0);
    }

    private class DeleteWorklogExecuteAction extends OkAction { }

}
