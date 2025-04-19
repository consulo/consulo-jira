package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.jira.rest.model.JiraProject;
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
import static com.intellij.jira.util.JiraIssueField.KEY;
import static com.intellij.tasks.jira.JiraUtil.GSON;
import static consulo.util.collection.ContainerUtil.getFirstItem;
import static consulo.util.lang.StringUtil.isEmpty;
import static consulo.util.lang.StringUtil.trim;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class ProjectSelectFieldEditor extends DataSelectFieldEditor<JiraProject> {

    public ProjectSelectFieldEditor(String fieldName, Object fieldValue, boolean required, boolean isMultiSelect, List<JiraProject> items) {
        super(fieldName, fieldValue, required, isMultiSelect, items);
        myButtonAction = new ProjectPickerDialogAction();
    }

    @Override
    protected void initSelectedItems() {
        JsonElement element = GSON.toJsonTree(myFieldValue);
        if (element.isJsonArray()) {
            mySelectedItems = JiraGsonUtil.getAsList(element, JiraProject[].class);
        } else if (element.isJsonObject()) {
            mySelectedItems = Collections.singletonList(GSON.fromJson(element, JiraProject.class));
        }
    }

    @Override
    public JComponent createPanel() {
        if (Objects.nonNull(mySelectedItems)) {
            myTextField.setText(mySelectedItems.stream().map(JiraProject::getKey).collect(joining(", ")));
        }

        return super.createPanel();
    }

    @Override
    public JsonElement getJsonValue() {
        if(isEmpty(trim(myTextField.getText()))){
            return JsonNull.INSTANCE;
        }

        List<String> values = mySelectedItems.stream().map(JiraProject::getKey).collect(toList());
        if(myIsMultiSelect){
            return createArrayObject(KEY, values);
        }

        return createObject(KEY, getFirstItem(values));
    }

    @Override
    public JiraProject getFieldValue() {
        return null;
    }


    class ProjectPickerDialogAction extends PickerDialogAction{

        ProjectPickerDialogAction() { }

        @Override
        public void actionPerformed(AnActionEvent e) {
            Project project = e.getData(Project.KEY);
            if(nonNull(project)){
                new ProjectPickerDialog(project).show();
            }
        }
    }



    class ProjectPickerDialog extends PickerDialog<JiraProject>{

        ProjectPickerDialog(@Nullable Project project) {
            super(project, "Projects", myItems, mySelectedItems);
        }

        @Override
        protected void doOKAction() {
            mySelectedItems = myList.getSelectedValuesList();
            myTextField.setText(mySelectedItems.isEmpty() ? "" :  mySelectedItems.stream().map(JiraProject::getKey).collect(joining(", ")));


            super.doOKAction();
        }
    }

}
