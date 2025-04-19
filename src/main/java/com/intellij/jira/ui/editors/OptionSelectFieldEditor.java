package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.jira.rest.model.JiraCustomFieldOption;
import com.intellij.jira.util.JiraGsonUtil;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.intellij.jira.util.JiraGsonUtil.createArrayObject;
import static com.intellij.jira.util.JiraGsonUtil.createObject;
import static com.intellij.tasks.jira.JiraRepository.GSON;
import static consulo.util.collection.ContainerUtil.getFirstItem;
import static consulo.util.lang.StringUtil.trim;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class OptionSelectFieldEditor extends DataSelectFieldEditor<JiraCustomFieldOption> {

    public OptionSelectFieldEditor(String fieldName, Object fieldValue, boolean required, boolean isMultiSelect, List<JiraCustomFieldOption> items) {
        super(fieldName, fieldValue, required, isMultiSelect, items);
        myButtonAction = new OptionPickerDialogAction();
    }

    @Override
    protected void initSelectedItems() {
        JsonElement element = GSON.toJsonTree(myFieldValue);
        if (element.isJsonArray()) {
            mySelectedItems = JiraGsonUtil.getAsList(element, JiraCustomFieldOption[].class);
        } else if (element.isJsonObject()) {
            mySelectedItems = Collections.singletonList(GSON.fromJson(element, JiraCustomFieldOption.class));
        }
    }

    @Override
    public JsonElement getJsonValue() {
        if(isEmpty(trim(myTextField.getText()))){
            return JsonNull.INSTANCE;
        }

        List<String> values = mySelectedItems.stream().map(JiraCustomFieldOption::getValue).collect(toList());
        if(myIsMultiSelect){
            return createArrayObject("value", values);
        }

        return createObject("value", getFirstItem(values));
    }

    @Override
    public JComponent createPanel() {
        if (Objects.nonNull(mySelectedItems)) {
            myTextField.setText(mySelectedItems.stream().map(JiraCustomFieldOption::getValue).collect(joining(", ")));
        }

        return super.createPanel();
    }

    @Override
    public JiraCustomFieldOption getFieldValue() {
        return null;
    }

    class OptionPickerDialogAction extends PickerDialogAction{

        OptionPickerDialogAction() { }

        @Override
        public void actionPerformed(AnActionEvent e) {
            Project project = e.getData(Project.KEY);
            if(nonNull(project)){
                new OptionPickerDialog(project).show();
            }
        }
    }



    class OptionPickerDialog extends PickerDialog<JiraCustomFieldOption>{

        OptionPickerDialog(@Nullable Project project) {
            super(project, "Options", myItems, mySelectedItems);
        }

        @Override
        protected void doOKAction() {
            mySelectedItems = myList.getSelectedValuesList();
            myTextField.setText(mySelectedItems.isEmpty() ? "" :  mySelectedItems.stream().map(JiraCustomFieldOption::getValue).collect(joining(", ")));

            super.doOKAction();
        }
    }


}
