package com.intellij.jira.ui.editors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import static com.intellij.jira.util.JiraGsonUtil.createObject;
import static consulo.util.lang.StringUtil.isEmpty;
import static consulo.util.lang.StringUtil.trim;

public class CommentFieldEditor extends TextAreaFieldEditor {

    public static final CommentFieldEditor INSTANCE = new CommentFieldEditor();

    public CommentFieldEditor() {
        super("Comment", null, false);
    }

    @Override
    public JsonElement getJsonValue() {
        if (isEmpty(trim(myTextArea.getText()))) {
            return JsonNull.INSTANCE;
        }

        JsonArray array = new JsonArray();
        JsonObject addObject = new JsonObject();
        addObject.add("add", createObject("body", myTextArea.getText()));
        array.add(addObject);

        return array;
    }
}
