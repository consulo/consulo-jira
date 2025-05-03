package com.intellij.jira.ui.dialog;

import com.intellij.jira.helper.TransitionFieldHelper;
import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.jira.tasks.EditWorklogTask;
import com.intellij.jira.ui.editors.*;
import consulo.project.Project;
import consulo.ui.ex.awt.DialogWrapper;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.ValidationInfo;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class EditWorklogDialog extends DialogWrapper {

    protected final Project myProject;
    protected String myIssueKey;
    protected List<String> myProjectRoles;
    private JiraIssueWorklog myWorklog;

    protected TimeSpentEditor myTimeSpentEditor;
    protected DateTimeFieldEditor myStartedEditor;
    protected RemainingEstimateFieldEditor myRemainingEstimateEditor;
    protected TextAreaFieldEditor myWorklogCommentEditor;
    protected VisibilityFieldEditor myVisibilityEditor;

    protected List<TransitionFieldHelper.FieldEditorInfo> myWorklogFields = new ArrayList<>();

    private JiraIssueTimeTracking myTimeTracking;
    private boolean myShowManualField;

    public EditWorklogDialog(@Nullable Project project, String issueKey, List<String> projectRoles, JiraIssueWorklog worklog, JiraIssueTimeTracking timeTracking, boolean showManualField) {
        super(project, false);
        myProject = project;
        myIssueKey = issueKey;
        myProjectRoles = projectRoles;
        myProjectRoles.add(0, "All Users");
        myWorklog = worklog;
        myTimeTracking = timeTracking;
        myShowManualField = showManualField;

        setTitle("Edit Log Work: " + issueKey);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        myTimeSpentEditor = new TimeSpentEditor(myWorklog.getTimeSpent(), true);
        myStartedEditor = new DateTimeFieldEditor("Date Started", myWorklog.getStarted(), true);
        myRemainingEstimateEditor = new RemainingEstimateFieldEditor("Remaining Estimate", false, myTimeTracking, myShowManualField);
        myWorklogCommentEditor = new TextAreaFieldEditor("Work Description", myWorklog.getComment(), false);
        myVisibilityEditor = new VisibilityFieldEditor(myWorklog.getVisibility(), myProjectRoles);

        myWorklogFields.add(TransitionFieldHelper.createFieldEditorInfo("timeSpentSeconds", myTimeSpentEditor));
        myWorklogFields.add(TransitionFieldHelper.createFieldEditorInfo("started", myStartedEditor));
        myWorklogFields.add(TransitionFieldHelper.createFieldEditorInfo("comment", myWorklogCommentEditor));
        myWorklogFields.add(TransitionFieldHelper.createFieldEditorInfo("visibility", myVisibilityEditor));

        return FormBuilder.createFormBuilder()
                .setVerticalGap(10)
                .addComponent(myTimeSpentEditor.createPanel())
                .addComponent(myStartedEditor.createPanel())
                .addComponent(myRemainingEstimateEditor.createPanel())
                .addComponent(myWorklogCommentEditor.createPanel())
                .addComponent(myVisibilityEditor.createPanel())
                .getPanel();
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return myTimeSpentEditor.getTextField();
    }

    @Nonnull
    @Override
    protected Action[] createActions() {
        return new Action[]{new EditWorklogExecuteAction(), myCancelAction};
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        for(TransitionFieldHelper.FieldEditorInfo info : myWorklogFields){
            ValidationInfo fieldValidation = info.validateField();
            if(nonNull(fieldValidation)){
                return fieldValidation;
            }
        }

        return myRemainingEstimateEditor.validate();
    }

    @Override
    protected void doOKAction() {
        if(nonNull(myProject)){
            new EditWorklogTask(myProject, myIssueKey, myWorklog.getId(), myWorklogFields, myRemainingEstimateEditor.getJsonValue()).queue();
        }

        close(0);
    }

    private class EditWorklogExecuteAction extends OkAction{ }

}
