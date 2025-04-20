package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;

public interface FieldEditor extends Editor {

     JsonElement getJsonValue();

     boolean isRequired();

}
