package com.intellij.jira.ui.editors.factory;

import com.google.gson.JsonArray;
import com.intellij.jira.rest.model.JiraCustomFieldOption;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueFieldProperties;
import com.intellij.jira.rest.model.JiraIssuePriority;
import com.intellij.jira.rest.model.JiraIssueResolution;
import com.intellij.jira.rest.model.JiraProject;
import com.intellij.jira.rest.model.JiraProjectComponent;
import com.intellij.jira.rest.model.JiraProjectVersion;
import com.intellij.jira.ui.editors.AttachmentFieldEditor;
import com.intellij.jira.ui.editors.ComboBoxFieldEditor;
import com.intellij.jira.ui.editors.DateFieldEditor;
import com.intellij.jira.ui.editors.DateTimeFieldEditor;
import com.intellij.jira.ui.editors.FieldEditor;
import com.intellij.jira.ui.editors.GroupSelectFieldEditor;
import com.intellij.jira.ui.editors.LabelFieldEditor;
import com.intellij.jira.ui.editors.LabelsFieldEditor;
import com.intellij.jira.ui.editors.LinkedIssueFieldEditor;
import com.intellij.jira.ui.editors.LogWorkFieldEditor;
import com.intellij.jira.ui.editors.MultiSelectFieldEditor;
import com.intellij.jira.ui.editors.NumberFieldEditor;
import com.intellij.jira.ui.editors.OptionSelectFieldEditor;
import com.intellij.jira.ui.editors.ProjectSelectFieldEditor;
import com.intellij.jira.ui.editors.TextAreaFieldEditor;
import com.intellij.jira.ui.editors.TextFieldEditor;
import com.intellij.jira.ui.editors.TimeTrackingFieldEditor;
import com.intellij.jira.ui.editors.UserSelectFieldEditor;
import com.intellij.jira.ui.editors.VersionSelectFieldEditor;
import com.intellij.jira.util.JiraGsonUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.intellij.jira.util.JiraGsonUtil.isEmpty;
import static com.intellij.jira.util.JiraIssueField.ASSIGNEE;
import static com.intellij.jira.util.JiraIssueField.ATTACHMENT;
import static com.intellij.jira.util.JiraIssueField.COMPONENT;
import static com.intellij.jira.util.JiraIssueField.DESCRIPTION;
import static com.intellij.jira.util.JiraIssueField.DUEDATE;
import static com.intellij.jira.util.JiraIssueField.ENVIRONMENT;
import static com.intellij.jira.util.JiraIssueField.FIX_VERSIONS;
import static com.intellij.jira.util.JiraIssueField.ISSUE_LINKS;
import static com.intellij.jira.util.JiraIssueField.ISSUE_TYPE;
import static com.intellij.jira.util.JiraIssueField.LABELS;
import static com.intellij.jira.util.JiraIssueField.PRIORITY;
import static com.intellij.jira.util.JiraIssueField.PROJECT;
import static com.intellij.jira.util.JiraIssueField.REPORTER;
import static com.intellij.jira.util.JiraIssueField.RESOLUTION;
import static com.intellij.jira.util.JiraIssueField.SUMMARY;
import static com.intellij.jira.util.JiraIssueField.TIME_TRACKING;
import static com.intellij.jira.util.JiraIssueField.VERSION;
import static com.intellij.jira.util.JiraIssueField.VERSIONS;
import static com.intellij.jira.util.JiraIssueField.WORKLOG;
import static java.util.Objects.isNull;

/**
 * Implementation of {@link FieldEditorFactory} to manage the creation of fields
 * for editing/transiting issues
 */
public class UpdateFieldEditorFactory implements FieldEditorFactory {

    protected static final Set<String> TEXT_AREA_FIELDS = Set.of(DESCRIPTION, ENVIRONMENT);
    protected static final Set<String> TEXT_FIELDS = Set.of(SUMMARY);
    protected static final Set<String> DATE_FIELDS = Set.of(DUEDATE);
    protected static final Set<String> USER_PICKER_FIELDS = Set.of(ASSIGNEE, REPORTER);

    private final JiraIssue myIssue;

    public UpdateFieldEditorFactory(JiraIssue issue) {
        myIssue = issue;
    }

    @Override
    public FieldEditor create(Project project, JiraIssueFieldProperties properties) {
        if (properties.getSchema().isCustomField()) {
            return createCustomFieldEditor(properties);
        }

        String fieldName = properties.getSchema().getSystem();
        if (TEXT_FIELDS.contains(fieldName)) {
            return getTextFieldEditor(properties, fieldName);
        } else if (TEXT_AREA_FIELDS.contains(fieldName)) {
            return getTextAreaFieldEditor(properties, fieldName);
        } else if (DATE_FIELDS.contains(fieldName)) {
            return getDateFieldEditor(properties, fieldName);
        } else if (USER_PICKER_FIELDS.contains(fieldName)) {
            return getUserSelectFieldEditor(properties, fieldName);
        } else if (TIME_TRACKING.equals(fieldName)) {
            return new TimeTrackingFieldEditor(properties.isRequired());
        } else if (ISSUE_LINKS.equals(fieldName)) {
            return new LinkedIssueFieldEditor(properties.getName(), properties.isRequired());
        } else if (ISSUE_TYPE.equals(fieldName)) {
            return new LabelFieldEditor(properties.getName(), myIssue.getIssuetype().getName());
        } else if(WORKLOG.equals(fieldName)) {
            return getLogWorkFieldEditor(properties);
        } else if (LABELS.equals(fieldName)) {
            return new LabelsFieldEditor(project, properties.getName(), properties.getAutoCompleteUrl());
        } else if (ATTACHMENT.equals(fieldName)) {
            return new AttachmentFieldEditor(properties.getName(), properties.isRequired());
        }

        return createCustomComboBoxFieldEditor(project, properties);
    }

    protected TextFieldEditor getTextFieldEditor(JiraIssueFieldProperties properties, String fieldName) {
        return new TextFieldEditor(properties.getName(), myIssue.getAsString(fieldName), properties.isRequired());
    }

    protected TextAreaFieldEditor getTextAreaFieldEditor(JiraIssueFieldProperties properties, String fieldName) {
        return new TextAreaFieldEditor(properties.getName(), myIssue.getAsString(fieldName), properties.isRequired());
    }

    protected DateFieldEditor getDateFieldEditor(JiraIssueFieldProperties properties, String fieldName) {
        return new DateFieldEditor(properties.getName(), myIssue.getAsDate(fieldName), properties.isRequired());
    }

    protected UserSelectFieldEditor getUserSelectFieldEditor(JiraIssueFieldProperties properties, String fieldName) {
        return new UserSelectFieldEditor(properties.getName(), myIssue.getAsJiraIssueUser(fieldName), properties.isRequired());
    }

    protected LogWorkFieldEditor getLogWorkFieldEditor(JiraIssueFieldProperties properties) {
        return new LogWorkFieldEditor(properties.getName(), myIssue.getTimetracking(), properties.isRequired());
    }

    protected FieldEditor createCustomComboBoxFieldEditor(Project project, JiraIssueFieldProperties properties) {
        JsonArray allowedValues = properties.getAllowedValues();
        if (isNull(allowedValues) || isEmpty(allowedValues)) {
            if (StringUtil.isEmpty(properties.getAutoCompleteUrl())) {
                return new LabelFieldEditor(properties.getName());
            } else {
                return new LabelsFieldEditor(project, properties.getName(), properties.getAutoCompleteUrl());
            }
        }

        List<?> items = new ArrayList<>();
        Object selectedItem = null;
        boolean isArray = properties.getSchema().isArray();
        String type = isArray ? properties.getSchema().getItems() : properties.getSchema().getType();
        String fieldName = properties.getSchema().getSystem();
        if (PRIORITY.equals(type)) {
            items = JiraGsonUtil.getAsList(allowedValues, JiraIssuePriority[].class);
            selectedItem = myIssue.getPriority();
        } else if (VERSION.equals(type)) {
            items = JiraGsonUtil.getAsList(allowedValues, JiraProjectVersion[].class);
            if (FIX_VERSIONS.equals(fieldName)) {
                selectedItem = myIssue.getFixVersions();
            } else if (VERSIONS.equals(fieldName)) {
                selectedItem = myIssue.getVersions();
            }
        } else if (RESOLUTION.equals(type)) {
            items = JiraGsonUtil.getAsList(allowedValues, JiraIssueResolution[].class);
            selectedItem = myIssue.getResolution();
        } else if (COMPONENT.equals(type)) {
            items = JiraGsonUtil.getAsList(allowedValues, JiraProjectComponent[].class);
            selectedItem = myIssue.getComponents();
        }

        if (isArray) {
            return new MultiSelectFieldEditor<>(properties.getName(), items, selectedItem, properties.isRequired());
        }

        return new ComboBoxFieldEditor<>(properties.getName(), selectedItem, properties.isRequired(), items);
    }

    protected FieldEditor createCustomFieldEditor(JiraIssueFieldProperties properties) {

        boolean isArray = properties.getSchema().isArray();
        String type = isArray ? properties.getSchema().getItems() : properties.getSchema().getType();
        String customFieldType = properties.getSchema().getCustom();

        if (!isArray) {
            if ("string".equals(type)) {
                if ("textarea".equals(customFieldType)) {
                    return new TextAreaFieldEditor(properties.getName(), null, properties.isRequired());
                }

                return new TextFieldEditor(properties.getName(), null, properties.isRequired());
            } else if ("number".equals(type)) {
                return new NumberFieldEditor(properties.getName(), null, properties.isRequired());
            } else if ("date".equals(type)) {
                return new DateFieldEditor(properties.getName(), null, properties.isRequired());
            } else if ("datetime".equals(type)) {
                return new DateTimeFieldEditor(properties.getName(), null,  properties.isRequired());
            }
        }

        // The field has not values so we have to retrieve them
        JsonArray values = properties.getAllowedValues();
        if (isNull(values) || isEmpty(values)) {
            if ("user".equals(type)) {
                return new UserSelectFieldEditor(properties.getName(), null, properties.isRequired(), isArray);
            } else if ("group".equals(type)) {
                return new GroupSelectFieldEditor(properties.getName(), null, properties.isRequired(), isArray);
            } else {
                return new LabelFieldEditor(properties.getName());
            }
        }

        // The field has values
        if (PROJECT.equals(type)) {
            List<JiraProject> projects = JiraGsonUtil.getAsList(values, JiraProject[].class);
            return getProjectSelectFieldEditor(properties, isArray, projects);
        } else if (VERSION.equals(type)) {
            List<JiraProjectVersion> versions = JiraGsonUtil.getAsList(values, JiraProjectVersion[].class);
            return getVersionSelectFieldEditor(properties, isArray, versions);
        }

        List<JiraCustomFieldOption> options = JiraGsonUtil.getAsList(values, JiraCustomFieldOption[].class);
        return new OptionSelectFieldEditor(properties.getName(), null, properties.isRequired(), isArray, options);
    }

    protected ProjectSelectFieldEditor getProjectSelectFieldEditor(JiraIssueFieldProperties properties, boolean isArray, List<JiraProject> projects) {
        return new ProjectSelectFieldEditor(properties.getName(), myIssue.getCustomfieldValue(properties.getSchema().getCustomId()), properties.isRequired(), isArray, projects);
    }

    protected VersionSelectFieldEditor getVersionSelectFieldEditor(JiraIssueFieldProperties properties, boolean isArray, List<JiraProjectVersion> versions) {
        return new VersionSelectFieldEditor(properties.getName(), myIssue.getCustomfieldValue(properties.getSchema().getCustomId()), properties.isRequired(), isArray, versions);
    }

}
