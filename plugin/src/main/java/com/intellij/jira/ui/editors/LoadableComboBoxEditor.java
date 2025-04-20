package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import consulo.ui.ex.awt.ComboBox;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.ValidationInfo;
import consulo.util.collection.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Set;

import static com.intellij.jira.util.JiraGsonUtil.createNameObject;
import static java.util.Objects.isNull;

public class LoadableComboBoxEditor<T> extends LoadableFieldEditor<T> {

    protected JComboBox<T> myComboBox;

    public LoadableComboBoxEditor(String fieldName, boolean required) {
        super(fieldName, required);
        createComboBox();
    }

    protected void createComboBox() {
        myComboBox = new ComboBox<>(300);
    }

    @Override
    protected void doUpdateValues(@NotNull Set<T> values) {
        Object selected = myComboBox.getSelectedItem();
        myComboBox.removeAllItems();

        for (T value : values) {
            myComboBox.addItem(value);
        }

        myComboBox.updateUI();
        myComboBox.setSelectedItem(Objects.isNull(selected) ? ContainerUtil.getFirstItem(values) : selected);
    }

    public T getSelectedValue() {
        return (T) myComboBox.getSelectedItem();
    }

    public boolean hasSelectedValue() {
        return Objects.nonNull(getSelectedValue());
    }

    public void addActionListener(ActionListener listener) {
        myComboBox.addActionListener(listener);
    }

    @Override
    public T getFieldValue() {
        return (T) myFieldValue;
    }

    @Override
    public JComponent createPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(myComboBox);
        panel.add(myLoadingIcon);

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(myLabel, panel, true)
                .getPanel();
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if(isRequired() && Objects.nonNull(getSelectedValue())){
            return new ValidationInfo(myLabel.getText() + " is required.");
        }

        return null;
    }

    @Override
    public JsonElement getJsonValue() {
        if(isNull(myComboBox.getSelectedItem())){
            return JsonNull.INSTANCE;
        }

        return createNameObject(getSelectedValueAsString());
    }

    @NotNull
    public String getSelectedValueAsString(){
        return hasSelectedValue() ? getSelectedValue().toString() : "";
    }
}
