package com.intellij.jira.ui.editors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.ValidationInfo;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.util.Objects;

public class LogWorkFieldEditor extends AbstractFieldEditor<String> {

    protected TimeSpentEditor myTimeSpentEditor;
    protected DateTimeFieldEditor myStartedEditor;
    protected RemainingEstimateFieldEditor myRemainingEstimateEditor;

    private JiraIssueTimeTracking myTimeTracking;

    public LogWorkFieldEditor(String fieldName, JiraIssueTimeTracking timeTracking, boolean required) {
        super(fieldName, null, required);
        myTimeTracking = timeTracking;
    }

    @Override
    public String getFieldValue() {
        return null;
    }

    @Override
    public JComponent createPanel() {
        myTimeSpentEditor = new TimeSpentEditor();
        myStartedEditor = new DateTimeFieldEditor("Date Started", null, false);
        myRemainingEstimateEditor = new RemainingEstimateTransitionFieldEditor("Remaining Estimate", false, myTimeTracking);

        JPanel myWorkLogPanel = FormBuilder.createFormBuilder()
                .addComponent(myTimeSpentEditor.createPanel())
                .addComponent(myStartedEditor.createPanel())
                .addComponent(myRemainingEstimateEditor.createPanel())
                .getPanel();

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(myLabel, myWorkLogPanel, true)
                .getPanel();
    }

    @Override
    public JsonElement getJsonValue() {
        if (StringUtil.isEmpty(StringUtil.trim(myTimeSpentEditor.getTextField().getText()))){
            return JsonNull.INSTANCE;
        }

        JsonArray array = new JsonArray();
        JsonObject addObject = new JsonObject();

        JsonObject logWork = myRemainingEstimateEditor.getJsonValue().getAsJsonObject();
        logWork.add("timeSpentSeconds", myTimeSpentEditor.getJsonValue());
        logWork.add("started", myStartedEditor.getJsonValue());

        addObject.add("add", logWork);
        array.add(addObject);

        return array;
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        ValidationInfo info = myTimeSpentEditor.validate();
        if(Objects.nonNull(info)) {
            return info;
        }

        info = this.myStartedEditor.validate();
        if(Objects.nonNull(info)) {
            return info;
        }

        return myRemainingEstimateEditor.validate();
    }
}
