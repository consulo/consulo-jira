package com.intellij.jira.helper;

import com.google.gson.JsonElement;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueFieldProperties;
import com.intellij.jira.ui.editors.CommentFieldEditor;
import com.intellij.jira.ui.editors.FieldEditor;
import com.intellij.jira.ui.editors.factory.UpdateFieldEditorFactory;
import consulo.project.Project;
import consulo.ui.ex.awt.ValidationInfo;

import javax.swing.*;

import static com.intellij.jira.util.JiraIssueField.COMMENT;

public final class TransitionFieldHelper {


    private TransitionFieldHelper() { }

    public static FieldEditorInfo createFieldEditorInfo(Project project, JiraIssueFieldProperties properties, JiraIssue issue){
        return new FieldEditorInfo(project, properties, issue);
    }

    public static FieldEditorInfo createFieldEditorInfo(String jsonFieldName, FieldEditor fieldEditor){
        return new FieldEditorInfo(jsonFieldName, fieldEditor);
    }

    public static FieldEditorInfo createCommentFieldEditorInfo(){
        return new FieldEditorInfo(COMMENT, CommentFieldEditor.INSTANCE);
    }

    public static class FieldEditorInfo {

        private final FieldEditor myEditor;
        private final String myName;

        private FieldEditorInfo(Project project, JiraIssueFieldProperties properties, JiraIssue issue) {
            this(properties.getSchema().getFieldName(), new UpdateFieldEditorFactory(issue).create(project, properties));
        }

        private FieldEditorInfo(String jsonFieldName, FieldEditor fieldEditor) {
            myName = jsonFieldName;
            myEditor = fieldEditor;
        }

        public JComponent getPanel(){
            return myEditor.createPanel();
        }

        public JsonElement getJsonValue(){
            return myEditor.getJsonValue();
        }

        public String getName(){
            return myName;
        }

        public boolean isRequired(){
            return myEditor.isRequired();
        }

        public ValidationInfo validateField(){
            return myEditor.validate();
        }

        public FieldEditor getEditor() {
            return myEditor;
        }

    }

}
