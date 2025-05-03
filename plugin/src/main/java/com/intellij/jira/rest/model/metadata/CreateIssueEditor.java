package com.intellij.jira.rest.model.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.intellij.jira.components.CreateIssueMetaProvider;
import com.intellij.jira.helper.TransitionFieldHelper;
import com.intellij.jira.rest.model.JiraIssueFieldProperties;
import com.intellij.jira.rest.model.JiraIssueType;
import com.intellij.jira.rest.model.JiraProject;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.editors.Editor;
import com.intellij.jira.ui.editors.FieldEditor;
import com.intellij.jira.ui.editors.LoadableComboBoxEditor;
import com.intellij.jira.ui.editors.LoadableFieldEditor;
import com.intellij.jira.ui.editors.factory.CreateFieldEditorFactory;
import com.intellij.jira.util.JiraBorders;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.tasks.jira.JiraUtil;
import consulo.project.Project;
import consulo.ui.ex.awt.*;
import consulo.util.collection.ContainerUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.intellij.jira.util.JiraGsonUtil.createIdObject;
import static com.intellij.jira.util.JiraIssueField.ISSUE_TYPE;
import static com.intellij.jira.util.JiraIssueField.PROJECT;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class CreateIssueEditor implements Editor {

    private final Project myProject;
    private final CreateIssueMetaProvider myCreateIssueMetaProvider;
    private final ProjectComboBox myProjectCombo;
    private final IssueTypeComboBox myIssueTypeCombo;

    private FieldsEditor myFieldsEditor;
    private BorderLayoutPanel myFieldsPanel;

    public CreateIssueEditor(@Nonnull Project project, JiraRestApi jiraRestApi) {
        myProject = project;
        myCreateIssueMetaProvider = new CreateIssueMetaProvider(jiraRestApi);

        myProjectCombo = new ProjectComboBox();
        myProjectCombo.addActionListener(e -> updateIssueTypesCombo());

        myIssueTypeCombo = new IssueTypeComboBox();
        myIssueTypeCombo.addActionListener(e -> updateFieldsInForm());

        myProjectCombo.setDataProvider(new LoadableFieldEditor.DataProvider<>() {
            @Override
            public @Nullable Set<JiraProject> getCachedValues() {
                return myCreateIssueMetaProvider.getCachedValues();
            }

            @Override
            public void updateValuesAsynchronously() {
                myCreateIssueMetaProvider.updateValuesAsynchronously(CreateIssueEditor.this);
            }
        });

        myProjectCombo.reloadValuesInBackground();

    }

    @Override
    public JComponent createPanel() {
        myFieldsPanel = new BorderLayoutPanel();
        myFieldsPanel.setBorder(JiraBorders.emptyTop(10));

        JPanel projectIssueTypePanel = FormBuilder.createFormBuilder()
                .addComponent(myProjectCombo.createPanel())
                .addComponent(myIssueTypeCombo.createPanel())
                .addSeparator()
                .getPanel();

        BorderLayoutPanel panel = new BorderLayoutPanel();
        panel.setMinimumSize(JBUI.size(300, 450));
        panel.setPreferredSize(JBUI.size(400, 500));
        panel.addToTop(projectIssueTypePanel);
        panel.addToCenter(ScrollPaneFactory.createScrollPane(myFieldsPanel, true));

        return panel;
    }

    public String getProjectKey() {
        return myProjectCombo.hasSelectedValue() ? myProjectCombo.getSelectedValue().getKey() : null;
    }

    public Map<String, TransitionFieldHelper.FieldEditorInfo> getCreateIssueFields() {
        Map<String, TransitionFieldHelper.FieldEditorInfo> createIssueFields = myFieldsEditor.myCreateIssueFields;
        createIssueFields.put(PROJECT, TransitionFieldHelper.createFieldEditorInfo(PROJECT, myProjectCombo));
        createIssueFields.put(ISSUE_TYPE, TransitionFieldHelper.createFieldEditorInfo(ISSUE_TYPE, myIssueTypeCombo));

        return createIssueFields;
    }

    @Override
    public @Nullable ValidationInfo validate() {
        return myFieldsEditor.validate();
    }

    private void updateIssueTypesCombo() {
        myIssueTypeCombo.setDataProvider(new LoadableFieldEditor.DataProvider<>() {
            @Override
            public @Nullable Set<String> getCachedValues() {
                JiraProject selectedProject = myProjectCombo.getSelectedValue();
                if (selectedProject == null) {
                    return null;
                }

                List<JiraIssueTypeIssueCreateMetadata> issueTypeIssueCreateMetadata = myCreateIssueMetaProvider.getIssueTypes(selectedProject);
                return issueTypeIssueCreateMetadata.stream().map(JiraIssueType::getName).collect(Collectors.toSet());
            }

            @Override
            public void updateValuesAsynchronously() {

            }
        });
    }

    private void updateFieldsInForm() {
        myFieldsPanel.removeAll();

        JiraProject selectedProject = myProjectCombo.getSelectedValue();

        List<JiraIssueTypeIssueCreateMetadata> issueTypeIssueCreateMetadata = myCreateIssueMetaProvider.getIssueTypes(selectedProject);
        JiraIssueTypeIssueCreateMetadata firstIssueType = ContainerUtil.getFirstItem(issueTypeIssueCreateMetadata);

        myFieldsEditor = new FieldsEditor(firstIssueType.getFields());

        myFieldsPanel.addToCenter(myFieldsEditor.createPanel());

        myFieldsPanel.revalidate();
        myFieldsPanel.repaint();
    }

    public void onUpdateValues(Set<JiraProject> jiraProjects) {
        if (ContainerUtil.isEmpty(jiraProjects)) {
            myIssueTypeCombo.loaded();
            myFieldsPanel.removeAll();
            myFieldsPanel.addToCenter(JiraPanelUtil.createPlaceHolderPanel("No fields to display"));
            myFieldsPanel.revalidate();
            myFieldsPanel.repaint();
        } else {
            myProjectCombo.onUpdateValues(jiraProjects);
        }
    }


    private class ProjectComboBox extends LoadableComboBoxEditor<JiraProject> {

        public ProjectComboBox() {
            super("Project", true);
        }

        @Override
        public JsonElement getJsonValue() {
            if(isNull(getSelectedValue())){
                return JsonNull.INSTANCE;
            }

            return createIdObject(getSelectedValue().getId());
        }
    }

    private class IssueTypeComboBox extends LoadableComboBoxEditor<String> {

        public IssueTypeComboBox() {
            super("Issue Type", true);
        }

        public void loaded() {
            super.loaded();
        }

    }

    private class FieldsEditor implements Editor {

        private final JsonObject myFields;
        private final Map<String, TransitionFieldHelper.FieldEditorInfo> myCreateIssueFields = new HashMap<>();

        private FieldsEditor(JsonObject fields) {
            myFields = fields;
        }

        @Override
        public JComponent createPanel() {
            FormBuilder formBuilder = FormBuilder.createFormBuilder().setVerticalGap(10);

            List<JiraIssueFieldProperties> fields = myFields.entrySet().stream()
                    .filter(field -> !PROJECT.equals(field.getKey()) && !ISSUE_TYPE.equals(field.getKey()))
                    .map(entry -> JiraUtil.GSON.fromJson(entry.getValue(), JiraIssueFieldProperties.class))
                    .collect(Collectors.toList());

            fields.forEach(fieldProperties -> {
                FieldEditor fieldEditor = CreateFieldEditorFactory.getInstance().create(myProject, fieldProperties);
                TransitionFieldHelper.FieldEditorInfo info = TransitionFieldHelper.createFieldEditorInfo(fieldProperties.getSchema().getFieldName(), fieldEditor);
                myCreateIssueFields.put(info.getName(), info);

                formBuilder.addComponent(info.getPanel());
            });

            return formBuilder
                    .getPanel();
        }

        @Override
        public @Nullable ValidationInfo validate() {
            for(TransitionFieldHelper.FieldEditorInfo info : myCreateIssueFields.values()){
                ValidationInfo fieldValidation = info.validateField();
                if(nonNull(fieldValidation)){
                    return fieldValidation;
                }
            }

            return null;
        }

    }

}
