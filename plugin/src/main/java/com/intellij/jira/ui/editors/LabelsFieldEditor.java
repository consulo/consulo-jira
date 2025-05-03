package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.util.JiraGsonUtil;
import consulo.application.ApplicationManager;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.ui.awt.TextFieldWithAutoCompletion;
import consulo.project.Project;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.ValidationInfo;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;

public class LabelsFieldEditor extends AbstractFieldEditor<String> {

    private final Project myProject;
    private final String myAutoCompleteUrl;
    private TextFieldWithAutoCompletion<String> myTextFieldWithAutoCompletion;

    public LabelsFieldEditor(Project project, String fieldName, String autoCompleteUrl) {
        super(fieldName, null);
        myProject = project;
        myAutoCompleteUrl = autoCompleteUrl;
        myTextFieldWithAutoCompletion = new TextFieldWithAutoCompletion(project, new LabelsCompletionProvider(), true, "");
    }

    @Override
    public String getFieldValue() {
        return null;
    }

    @Override
    public JComponent createPanel() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(myLabel, myTextFieldWithAutoCompletion, true)
                .getPanel();
    }

    @Override
    public @Nullable ValidationInfo validate() {
        return null;
    }

    @Override
    public JsonElement getJsonValue() {
        String labels = myTextFieldWithAutoCompletion.getText();
        if (StringUtil.isEmpty(labels)) {
            return JsonNull.INSTANCE;
        }

        return JiraGsonUtil.createArray(Arrays.asList(labels.split(" ")));
    }


    private class LabelsCompletionProvider extends TextFieldWithAutoCompletion.StringsCompletionProvider {

        public LabelsCompletionProvider() {
            super(null, null);
        }

        @Override
        public @Nonnull Collection<String> getItems(String prefix, boolean cached, CompletionParameters parameters) {
            JiraServerManager manager = ApplicationManager.getApplication().getInstance(JiraServerManager.class);
            JiraRestApi jiraRestApi = manager.getJiraRestApi(myProject);

            return jiraRestApi.findLabels(prefix, myAutoCompleteUrl);
        }
    }

}
