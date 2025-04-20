package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import consulo.application.AllIcons;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.ValidationInfo;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.regex.Pattern;

import static com.intellij.jira.util.JiraGsonUtil.createPrimitive;
import static consulo.util.lang.StringUtil.isEmpty;
import static consulo.util.lang.StringUtil.isNotEmpty;
import static java.util.Objects.nonNull;

public class TimeTrackingFieldEditor extends AbstractFieldEditor {

    private static final Pattern TIME_TRACKING_PATTERN = Pattern.compile("((\\d+)[wdhm](\\s)*)+");
    private static final String ORIGINAL_ESTIMATE_FIELD = "Original Estimate";
    private static final String REMAINING_ESTIMATE_FIELD = "Remaining Estimate";
    private static final String INFO_MESSAGE = "You can specify a time unit after a time value 'X', such as Xw, Xd, Xh or Xm, to represent weeks (w), days (d), hours (h) and minutes (m), respectively.";

    private JPanel myFirstPanel;
    private JTextField myFirstField;
    private JLabel myFirstInfoLabel;

    private JPanel mySecondPanel;
    private MyLabel mySecondLabel;
    private JTextField mySecondField;
    private JLabel mySecondInfoLabel;

    public TimeTrackingFieldEditor(boolean required) {
        super(ORIGINAL_ESTIMATE_FIELD, null, required);
        mySecondLabel = new MyLabel(REMAINING_ESTIMATE_FIELD, required);
    }

    @Override
    public JComponent createPanel() {
        myFirstInfoLabel.setToolTipText(INFO_MESSAGE);
        myFirstInfoLabel.setIcon(TargetAWT.to(AllIcons.Actions.Help));

        mySecondInfoLabel.setToolTipText(INFO_MESSAGE);
        mySecondInfoLabel.setIcon(TargetAWT.to(AllIcons.Actions.Help));

        return FormBuilder.createFormBuilder()
                .addComponent(myLabel)
                .addComponent(myFirstPanel)
                .addComponent(mySecondLabel)
                .addComponent(mySecondPanel)
                .getPanel();
    }


    @Override
    public JsonElement getJsonValue() {
        if(isEmpty(getOriginalEstimate()) && isEmpty(getRemainingEstimate())) {
            return JsonNull.INSTANCE;
        }

        JsonObject timeObject = new JsonObject();
        if(isNotEmpty(getOriginalEstimate())){
            timeObject.add("originalEstimate", createPrimitive(getOriginalEstimate()));
        }

        if(isNotEmpty(getRemainingEstimate())){
            timeObject.add("remainingEstimate", createPrimitive(getRemainingEstimate()));
        }

        return timeObject;
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if(isRequired() && isEmpty(getOriginalEstimate()) ){
            return new ValidationInfo(myLabel.getText() + " is required.");
        }

        if(isRequired() && isEmpty(getRemainingEstimate())){
            return new ValidationInfo(mySecondLabel.getText() + " is required.");
        }

        if(isNotEmpty(getOriginalEstimate()) && !TIME_TRACKING_PATTERN.matcher(getOriginalEstimate()).matches()){
            return new ValidationInfo("Wrong format in " + myLabel.getText() + " field.");
        }

        if(isNotEmpty(getRemainingEstimate()) && !TIME_TRACKING_PATTERN.matcher(getRemainingEstimate()).matches()){
            return new ValidationInfo("Wrong format in " + mySecondLabel.getText() + " field.");
        }

        return null;
    }

    private String getOriginalEstimate(){
        return nonNull(myFirstField) ? myFirstField.getText() : "";
    }

    private String getRemainingEstimate(){
        return nonNull(mySecondField) ? mySecondField.getText() : "";
    }

    @Override
    public Object getFieldValue() {
        return null;
    }
}
