package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.jira.rest.model.JiraGroup;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.jira.util.JiraGsonUtil.createArrayNameObjects;
import static com.intellij.jira.util.JiraGsonUtil.createNameObject;
import static consulo.util.collection.ContainerUtil.getFirstItem;
import static consulo.util.lang.StringUtil.isEmpty;
import static consulo.util.lang.StringUtil.trim;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class GroupSelectFieldEditor extends SelectFieldEditor<String> {

    private List<String> mySelectedGroups = new ArrayList<>();

    public GroupSelectFieldEditor(String fieldName, Object fieldValue, boolean required, boolean isMultiSelect) {
        super(fieldName, fieldValue, required, isMultiSelect);
        myButtonAction = new GroupPickerDialogAction();
    }

    @Override
    public JsonElement getJsonValue() {
        if(isEmpty(trim(myTextField.getText()))){
            return JsonNull.INSTANCE;
        }

        if(myIsMultiSelect){
            return createArrayNameObjects(mySelectedGroups);
        }

        return createNameObject(getFirstItem(mySelectedGroups));
    }

    @Override
    public String getFieldValue() {
        return null;
    }

    private class GroupPickerDialogAction extends PickerDialogAction {

        public GroupPickerDialogAction() {
            super();
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            super.actionPerformed(e);
            if(nonNull(myJiraRestApi)){
                List<String> users = myJiraRestApi.getGroups().stream().map(JiraGroup::getName).collect(toList());
                GroupPickerDialog dialog = new GroupPickerDialog(myProject, users);
                dialog.show();
            }

        }
    }


    class GroupPickerDialog extends PickerDialog<String> {

        public GroupPickerDialog(@Nullable Project project, List<String> items) {
            super(project, "Groups", items, null);
        }

        @Override
        protected void doOKAction() {
            mySelectedGroups = myList.getSelectedValuesList();
            myTextField.setText(mySelectedGroups.isEmpty() ? "" : String.join(", ", mySelectedGroups));

            super.doOKAction();
        }
    }





}
