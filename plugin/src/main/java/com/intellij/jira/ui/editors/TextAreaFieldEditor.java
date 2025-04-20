package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import consulo.ui.ex.JBColor;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

import static com.intellij.jira.util.JiraGsonUtil.createPrimitive;
import static consulo.util.lang.StringUtil.isEmpty;
import static consulo.util.lang.StringUtil.trim;

public class TextAreaFieldEditor extends AbstractFieldEditor<String> {

    private JPanel myPanel;
    private JLabel myTextAreaLabel;
    protected JTextArea myTextArea;

    public TextAreaFieldEditor(String fieldName, Object fieldValue, boolean required) {
        super(fieldName, fieldValue, required);
    }

    @Override
    public JComponent createPanel() {
        myTextArea.setBorder(BorderFactory.createLineBorder(JBColor.border()));
        myTextArea.setText(getFieldValue());
        myTextAreaLabel.setText(myLabel.getText());

        return FormBuilder.createFormBuilder()
                .addComponent(myPanel)
                .getPanel();
    }

    @Override
    public JsonElement getJsonValue() {
        if(isEmpty(trim(myTextArea.getText()))){
            return JsonNull.INSTANCE;
        }

        return createPrimitive(myTextArea.getText());
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if(isRequired() && isEmpty(trim(myTextArea.getText()))){
            return new ValidationInfo(myLabel.getText() + " is required.");
        }

        return null;
    }

    @Override
    public String getFieldValue() {
        return Objects.nonNull(myFieldValue) ? (String) myFieldValue : "";
    }

}
