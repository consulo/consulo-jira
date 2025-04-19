package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import com.intellij.jira.util.JiraGsonUtil;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.JBTextField;
import consulo.ui.ex.awt.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class RemainingEstimateFieldEditor extends AbstractFieldEditor<String> {

    private JRadioButton myAutoButton;
    protected JRadioButton myLeaveButton;
    protected JRadioButton myNewButton;
    protected JRadioButton myManualButton;

    protected TextFieldEditor myNewTextFieldEditor;
    protected TextFieldEditor myManualTextFieldEditor;

    private JiraIssueTimeTracking myTimeTracking;
    private boolean myShowManualField;

    private JPanel myPanel;
    private JPanel myNewPanel;
    private JPanel myManualPanel;

    public RemainingEstimateFieldEditor(String fieldName, boolean required, JiraIssueTimeTracking timeTracking, boolean showManualField) {
        super(fieldName, null, required);
        myShowManualField = showManualField;
        myTimeTracking = timeTracking;
    }

    @Override
    public JComponent createPanel() {
        myLeaveButton.setText(getTimeTrackingText());

        myNewTextFieldEditor = new MyTimeSpentEditor("Set to");
        myManualTextFieldEditor = new MyTimeSpentEditor("Reduce by");

        myNewPanel.add(myNewTextFieldEditor.createPanel());

        if (myShowManualField) {
            myManualButton.setVisible(true);
            myManualPanel.add(myManualTextFieldEditor.createPanel());
        }

        return FormBuilder.createFormBuilder()
                    .addComponent(myPanel)
                    .getPanel();
    }

    @Override
    public JsonElement getJsonValue() {
        if (myNewButton.isSelected()) {
            return JiraGsonUtil.createPrimitive("new&newEstimate=" + myNewTextFieldEditor.getTextField().getText());
        } else if(myManualButton.isSelected()) {
            return JiraGsonUtil.createPrimitive("manual&increaseBy=" + myManualTextFieldEditor.getTextField().getText());
        } else if(myLeaveButton.isSelected()) {
            return JiraGsonUtil.createPrimitive("leave");
        }

        return JiraGsonUtil.createPrimitive("auto");
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
       if (myNewButton.isSelected()) {
           return myNewTextFieldEditor.validate();
       } else if (myManualButton.isSelected()) {
           return myManualTextFieldEditor.validate();
       }

       return null;
    }

    private String getTimeTrackingText(){
        return Objects.isNull(myTimeTracking) ? "Leave estimate unset" : "Use existing estimate of " + myTimeTracking.getRemainingEstimate();
    }

    @Override
    public String getFieldValue() {
        return null;
    }



    private static class MyTimeSpentEditor extends TimeSpentEditor {

        public MyTimeSpentEditor(String fieldName) {
            super(fieldName, "", false);
        }

        @Override
        public JComponent createPanel() {
            myTextField = new JBTextField();
            myTextField.setText(super.getFieldValue());

            return FormBuilder.createFormBuilder()
                    .addLabeledComponent(myLabel, getTextField())
                    .getPanel();
        }
    }

}
