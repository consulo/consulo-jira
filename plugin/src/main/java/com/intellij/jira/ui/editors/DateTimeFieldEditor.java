package com.intellij.jira.ui.editors;

import consulo.ui.ex.awt.ValidationInfo;
import jakarta.annotation.Nullable;

import javax.swing.text.DateFormatter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static consulo.util.lang.StringUtil.*;

public class DateTimeFieldEditor extends DateFieldEditor {

    private static final DateFormatter DATE_TIME = new DateFormatter(new SimpleDateFormat("yyyy-MM-dd HH:mm"));
    private static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final DateTimeFormatter ISO_8601 = DateTimeFormatter.ofPattern(ISO_8601_FORMAT);

    public DateTimeFieldEditor(String fieldName, Object fieldValue, boolean required) {
        super(fieldName, fieldValue, required);
    }

    @Override
    public String getToolTipMessage() {
        return "E.g. yyyy-MM-dd HH:mm";
    }

    @Override
    protected String getValue() {
        String dateTime = super.getValue();
        String[] words = dateTime.split(" ");
        if(words.length != 2){
            return "";
        }

        LocalDate ld = LocalDate.parse(words[0]);
        LocalTime lt = LocalTime.parse(words[1]);
        LocalDateTime inputDate = LocalDateTime.of(ld, lt);

        return inputDate.atZone(ZoneId.systemDefault()).format(ISO_8601);
    }

    @Override
    public DateFormatter getDateFormatter() {
        return DATE_TIME;
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if(isRequired() && isEmpty(trim(myFormattedTextField.getText()))){
            return new ValidationInfo(myLabel.getText() + " is required.");
        }else{
            if(isNotEmpty(trim(myFormattedTextField.getText()))){
                try{
                    LocalDateTime.parse(myFormattedTextField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                }catch (DateTimeParseException e){
                    return new ValidationInfo("Wrong format in " + myLabel.getText() + " field.");
                }
            }

        }

        return null;
    }

}
