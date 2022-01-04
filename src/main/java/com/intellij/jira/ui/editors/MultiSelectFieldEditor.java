package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.intellij.jira.util.JiraGsonUtil.createArrayNameObjects;
import static com.intellij.jira.util.JiraGsonUtil.createNameObject;
import static com.intellij.openapi.util.text.StringUtil.isEmpty;
import static com.intellij.openapi.util.text.StringUtil.trim;
import static com.intellij.util.containers.ContainerUtil.getFirstItem;
import static java.util.Objects.nonNull;

public class MultiSelectFieldEditor<T> extends SelectFieldEditor<T> {

    private List<String> mySelectedItems = new ArrayList<>();

    public MultiSelectFieldEditor(String fieldName, List<T> items, Object fieldValue, boolean required) {
        super(fieldName, fieldValue, required, true);
        List currentItems = Collections.emptyList();
        if (fieldValue instanceof List) {
            currentItems = (List) fieldValue;
        } else if (fieldValue != null) {
            currentItems = Collections.singletonList(fieldValue);
        }
        myButtonAction = new ItemPickerDialogAction(fieldName, items, currentItems);

        myTextField.setText(currentItems.isEmpty() ? "" : String.join(", ", toStringList(currentItems)));
    }

    @NotNull
    private List<String> toStringList(List items) {
        if (items == null) {
            return Collections.emptyList();
        }

        List<String> itemsToString = new ArrayList<>();
        for (Object item : items) {
            if (item == null) {
                continue;
            }
            itemsToString.add(item.toString());
        }
        return itemsToString;
    }

    @Override
    public JsonElement getJsonValue() {
        if (isEmpty(trim(myTextField.getText()))) {
            return JsonNull.INSTANCE;
        }

        if (myIsMultiSelect) {
            return createArrayNameObjects(mySelectedItems);
        }

        return createNameObject(getFirstItem(mySelectedItems));
    }

    @Override
    public T getFieldValue() {
        return null;
    }

    private class ItemPickerDialogAction extends PickerDialogAction {
        private List<T> items;
        private List<T> selectedItems;
        private String fieldName;

        public ItemPickerDialogAction(String fieldName, List<T> items, List<T> selectedItems) {
            super();

            this.fieldName = fieldName;
            this.items = items;
            this.selectedItems = selectedItems;
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            super.actionPerformed(e);
            if (nonNull(myJiraRestApi)) {
                List<String> items = toStringList(this.items);
                items.sort(String.CASE_INSENSITIVE_ORDER);

                List<String> selectedItems = toStringList(this.selectedItems);

                ItemPickerDialog dialog = new ItemPickerDialog(myProject, fieldName, items, selectedItems);
                dialog.show();
            }

        }
    }

    class ItemPickerDialog extends PickerDialog<String> {

        public ItemPickerDialog(@Nullable Project project, String fieldName, List<String> items, List<String> selectedItems) {
            super(project, fieldName, items, selectedItems);
        }

        @Override
        protected void doOKAction() {
            mySelectedItems = myList.getSelectedValuesList();
            myTextField.setText(mySelectedItems.isEmpty() ? "" : String.join(", ", mySelectedItems));

            super.doOKAction();
        }
    }
}
