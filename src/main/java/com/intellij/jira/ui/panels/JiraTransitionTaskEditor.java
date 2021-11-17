package com.intellij.jira.ui.panels;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.settings.ChangelistSettings;
import com.intellij.jira.tasks.AddChangelistTask;
import com.intellij.jira.ui.editors.Editor;
import com.intellij.jira.util.ChangelistUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.tasks.TaskBundle;
import com.intellij.ui.SeparatorFactory;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.Objects;

public class JiraTransitionTaskEditor implements Editor {

    private final Project project;
    private final JiraIssue issue;

    private JCheckBox createChangelistCheck;
    private JLabel changeListNameLabel;
    private JTextField changeListNameField;
    private JCheckBox setDefaultCheck;


    public JiraTransitionTaskEditor(Project project, JiraIssue issue) {
        this.project = project;
        this.issue = issue;
    }

    @Override
    public JComponent createPanel() {
        createChangelistCheck = new JCheckBox("Create a new changelist");
        createChangelistCheck.addActionListener(e -> updateField(createChangelistCheck.isSelected()));

        ChangelistSettings changelistSettings = ChangelistSettings.getInstance();
        String fieldValue = ChangelistUtil.getChangelistName(issue, changelistSettings);

        changeListNameLabel = new JLabel("Name:");
        changeListNameLabel.setEnabled(false);

        changeListNameField = new JTextField(50);
        changeListNameField.setText(fieldValue);
        changeListNameField.setEnabled(false);

        setDefaultCheck = new JCheckBox("Set Default", true);
        setDefaultCheck.setEnabled(false);

        JPanel newChangelistPanel = FormBuilder.createFormBuilder()
                                                    .addComponent(SeparatorFactory.createSeparator("Changelist", null))
                                                    .addComponent(createChangelistCheck)
                                                    .setFormLeftIndent(30)
                                                    .addLabeledComponent(changeListNameLabel, changeListNameField)
                                                    .addComponent(setDefaultCheck)
                                                    .getPanel();


        return FormBuilder.createFormBuilder()
                .addComponent(newChangelistPanel)
                .getPanel();
    }

    @Override
    public @Nullable ValidationInfo validate() {
        if (createChangelistCheck.isSelected() && changeListNameField.getText().trim().isEmpty()) {
            return new ValidationInfo(TaskBundle.message("dialog.message.changelist.name.should.not.be.empty"), changeListNameField);
        }

        return null;
    }

    public void doTask() {
        ValidationInfo info = validate();
        if (createChangelistCheck.isSelected() && Objects.isNull(info)) {
            new AddChangelistTask(project, getChangelistName(), isNewChangelistSetActive()).queue();
        }
    }

    private String getChangelistName() {
        return changeListNameField.getText().trim();
    }

    private boolean isNewChangelistSetActive() {
        return setDefaultCheck.isSelected();
    }

    private void updateField(boolean show) {
        changeListNameLabel.setEnabled(show);
        changeListNameField.setEnabled(show);
        setDefaultCheck.setEnabled(show);
    }

}
