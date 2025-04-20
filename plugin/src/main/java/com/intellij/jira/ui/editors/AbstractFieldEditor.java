package com.intellij.jira.ui.editors;

import consulo.ui.ex.awt.JBLabel;

import javax.swing.*;

public abstract class AbstractFieldEditor<T> implements FieldEditor {

    protected JLabel myLabel;
    protected Object myFieldValue;
    private final boolean myIsRequired;

    public AbstractFieldEditor(String fieldName, Object fieldValue) {
        this(fieldName, fieldValue, false);
    }

    public AbstractFieldEditor(String fieldName, Object fieldValue, boolean required) {
        myLabel = new MyLabel(fieldName, required);
        myFieldValue = fieldValue;
        myIsRequired = required;
    }

    public abstract T getFieldValue();

    @Override
    public boolean isRequired() {
        return myIsRequired;
    }

    class MyLabel extends JBLabel {
        private String myLabelText;

        public MyLabel(String labelText, boolean required) {
            super();
            myLabelText = labelText;
            setText(myLabelText + (required ? " *" : ""));
        }

    }

}
