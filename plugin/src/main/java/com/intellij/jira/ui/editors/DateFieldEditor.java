package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import consulo.application.AllIcons;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.ValidationInfo;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import jakarta.annotation.Nullable;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Objects;

import static com.intellij.jira.util.JiraGsonUtil.createPrimitive;
import static consulo.util.lang.StringUtil.*;
import static java.util.Objects.nonNull;

public class DateFieldEditor extends AbstractFieldEditor<String> {

    private static final DateFormatter DATE_FORMATTER = new DateFormatter(new SimpleDateFormat("yyyy-MM-dd"));

    private JPanel myPanel;
    protected JFormattedTextField myFormattedTextField;
    protected JLabel myInfoLabel;

    public DateFieldEditor(String fieldName, Object fieldValue, boolean required) {
        super(fieldName, fieldValue, required);
    }

    @Override
    public String getFieldValue() {
        try {
            return getDateFormatter().valueToString(Objects.nonNull(myFieldValue) ? myFieldValue : new Date());
        } catch (ParseException e) {
            return "";
        }
    }

    @Override
    public JComponent createPanel() {
        myFormattedTextField.setFormatterFactory(new DefaultFormatterFactory(getDateFormatter()));
        myFormattedTextField.setText(getFieldValue());

        myInfoLabel.setToolTipText(getToolTipMessage());
        myInfoLabel.setIcon(TargetAWT.to(AllIcons.Actions.Help));

        return FormBuilder.createFormBuilder()
                .addComponent(myLabel)
                .addComponent(myPanel)
                .getPanel();
    }


    public DateFormatter getDateFormatter() {
        return DATE_FORMATTER;
    }

    public String getToolTipMessage() {
        return "E.g. yyyy-MM-dd";
    }

    protected String getValue() {
        return nonNull(myFormattedTextField) ? trim(myFormattedTextField.getText()) : "";
    }

    @Override
    public JsonElement getJsonValue() {
        if (isEmpty(myFormattedTextField.getText())) {
            return JsonNull.INSTANCE;
        }

        return createPrimitive(getValue());
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if (isRequired() && isEmpty(trim(myFormattedTextField.getText()))) {
            return new ValidationInfo(myLabel.getText() + " is required.");
        } else {
            if (isNotEmpty(trim(myFormattedTextField.getText()))) {
                try {
                    LocalDate.parse(myFormattedTextField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (DateTimeParseException e) {
                    return new ValidationInfo("Wrong format in " + myLabel.getText() + " field.");
                }
            }
        }

        return null;
    }
}
