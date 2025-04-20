package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.JBTextField;
import consulo.ui.ex.awt.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

import static com.intellij.jira.util.JiraGsonUtil.createPrimitive;
import static consulo.util.lang.StringUtil.isEmpty;
import static consulo.util.lang.StringUtil.trim;

public class TextFieldEditor extends AbstractFieldEditor<String> {

    protected JTextField myTextField;

    public TextFieldEditor(String fieldName, Object fieldValue, boolean required) {
        super(fieldName, fieldValue, required);
    }

    @Override
    public String getFieldValue() {
        return Objects.nonNull(myFieldValue) ? (String) myFieldValue : "";
    }

    @Override
    public JComponent createPanel() {
        myTextField = new JBTextField();
        myTextField.setText(getFieldValue());

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(myLabel, myTextField, true)
                .getPanel();
    }

    @Override
    public JsonElement getJsonValue() {
        if(isEmpty(trim(myTextField.getText()))){
            return JsonNull.INSTANCE;
        }

        return createPrimitive(myTextField.getText());
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if(isRequired() && isEmpty(trim(myTextField.getText()))){
            return new ValidationInfo(myLabel.getText() + " is required");
        }

        return null;
    }

    public JTextField getTextField() {
        return myTextField;
    }
}
