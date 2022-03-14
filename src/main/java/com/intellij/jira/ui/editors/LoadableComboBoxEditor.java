package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Set;

import static com.intellij.jira.util.JiraGsonUtil.createNameObject;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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
        return nonNull(getSelectedValue()) ? getSelectedValue().toString() : "";
    }
}
