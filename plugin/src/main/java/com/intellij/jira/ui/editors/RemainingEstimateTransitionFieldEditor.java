package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.jira.rest.model.JiraIssueTimeTracking;

public class RemainingEstimateTransitionFieldEditor extends RemainingEstimateFieldEditor {

    private static final String ADJUST_ESTIMATE = "adjustEstimate";

    public RemainingEstimateTransitionFieldEditor(String fieldName, boolean required, JiraIssueTimeTracking timeTracking) {
        super(fieldName, required, timeTracking, true);
    }

    @Override
    public JsonElement getJsonValue() {
        JsonObject remainingEstimate = new JsonObject();
        if (myNewButton.isSelected()){
            remainingEstimate.addProperty(ADJUST_ESTIMATE, "new");
            remainingEstimate.addProperty("newEstimate", myNewTextFieldEditor.getTextField().getText());
        } else if (myManualButton.isSelected()){
            remainingEstimate.addProperty(ADJUST_ESTIMATE, "manual");
            remainingEstimate.addProperty("increaseBy", myManualTextFieldEditor.getTextField().getText());
        } else if (myLeaveButton.isSelected()){
            remainingEstimate.addProperty(ADJUST_ESTIMATE, "leave");
        } else {
            remainingEstimate.addProperty(ADJUST_ESTIMATE, "auto");
        }

        return remainingEstimate;
    }
}
