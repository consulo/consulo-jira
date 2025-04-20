package com.intellij.jira.ui.editors;

import java.util.List;


public abstract class DataSelectFieldEditor<T> extends SelectFieldEditor<T> {

    protected List<T> myItems;
    protected List<T> mySelectedItems;

    public DataSelectFieldEditor(String fieldName, Object fieldValue, boolean required, boolean isMultiSelect, List<T> items) {
        super(fieldName, fieldValue, required, isMultiSelect);
        myItems = items;
        initSelectedItems();
    }

    protected abstract void initSelectedItems();

}
