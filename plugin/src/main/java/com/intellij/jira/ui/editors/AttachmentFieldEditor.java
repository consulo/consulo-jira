package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

public class AttachmentFieldEditor extends FileChooserFieldEditor {

    public AttachmentFieldEditor(String fieldName, boolean required) {
        super(fieldName, required);
    }

    @Override
    public JsonElement getJsonValue() {
        return JsonNull.INSTANCE;
    }

}
