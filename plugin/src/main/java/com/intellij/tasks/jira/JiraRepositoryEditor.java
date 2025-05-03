// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.tasks.jira;

import com.intellij.tasks.jira.jql.JqlLanguage;
import consulo.language.editor.ui.awt.EditorTextField;
import consulo.language.editor.ui.awt.LanguageTextField;
import consulo.project.Project;
import consulo.task.TaskBundle;
import consulo.task.ui.BaseRepositoryEditor;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.JBLabel;
import consulo.ui.ex.awt.UIUtil;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * @author Mikhail Golubev
 */
public final class JiraRepositoryEditor extends BaseRepositoryEditor<JiraRepository> {
    private EditorTextField mySearchQueryField;
    private JBLabel mySearchLabel;
    private JBLabel myNoteLabel;
    private JCheckBox myUseBearerTokenAuthenticationCheckBox;

    public JiraRepositoryEditor(Project project, JiraRepository repository, Consumer<JiraRepository> changeListener) {
        super(project, repository, changeListener);
    }

    @Override
    public void apply() {
        myRepository.setSearchQuery(mySearchQueryField.getText());
        myRepository.setUseBearerTokenAuthentication(myUseBearerTokenAuthenticationCheckBox.isSelected());
        super.apply();
        adjustSettingsForServerProperties();
    }

    @Override
    protected void afterTestConnection(boolean connectionSuccessful) {
        super.afterTestConnection(connectionSuccessful);
        if (connectionSuccessful) {
            adjustSettingsForServerProperties();
        }
    }

    @Override
    protected @Nullable JComponent createCustomPanel() {
        mySearchQueryField = new LanguageTextField(JqlLanguage.INSTANCE, myProject, myRepository.getSearchQuery());
        installListener(mySearchQueryField);
        mySearchLabel = new JBLabel(TaskBundle.message("label.search"), SwingConstants.RIGHT);
        myNoteLabel = new JBLabel();
        myNoteLabel.setComponentStyle(UIUtil.ComponentStyle.SMALL);
        myUseBearerTokenAuthenticationCheckBox = new JCheckBox(TaskBundle.message("use.personal.access.token"));
        myUseBearerTokenAuthenticationCheckBox.setSelected(myRepository.isUseBearerTokenAuthentication());
        myUseBearerTokenAuthenticationCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                useBearerTokenChanged();
            }
        });

        adjustSettingsForServerProperties();
        return FormBuilder.createFormBuilder()
            .addComponentToRightColumn(myUseBearerTokenAuthenticationCheckBox)
            .addLabeledComponent(mySearchLabel, mySearchQueryField)
            .addComponentToRightColumn(myNoteLabel)
            .getPanel();
    }

    @Override
    public void setAnchor(final @Nullable JComponent anchor) {
        super.setAnchor(anchor);
        mySearchLabel.setAnchor(anchor);
    }

    private void adjustSettingsForServerProperties() {
        if (myRepository.isJqlSupported()) {
            mySearchQueryField.setEnabled(true);
            myNoteLabel.setVisible(false);
        }
        else {
            mySearchQueryField.setEnabled(false);
            myNoteLabel.setText(
                TaskBundle.message("label.jql.search.cannot.be.used.in.jira.versions.prior.your.version", myRepository.getPresentableVersion()));
            myNoteLabel.setVisible(true);
        }

        if (myRepository.isInCloud()) {
            myUsernameLabel.setVisible(true);
            myUserNameText.setVisible(true);
            myUsernameLabel.setText(TaskBundle.message("label.email"));
            myPasswordLabel.setText(TaskBundle.message("label.api.token"));
            myUseBearerTokenAuthenticationCheckBox.setVisible(false);
        }
        else if (myUseBearerTokenAuthenticationCheckBox.isSelected()) {
            myUsernameLabel.setVisible(false);
            myUserNameText.setVisible(false);
            myPasswordLabel.setText(TaskBundle.message("label.api.token"));
            myUseBearerTokenAuthenticationCheckBox.setVisible(true);
        }
        else {
            myUsernameLabel.setVisible(true);
            myUserNameText.setVisible(true);
            myUsernameLabel.setText(TaskBundle.message("label.username"));
            myPasswordLabel.setText(TaskBundle.message("label.password"));
            myUseBearerTokenAuthenticationCheckBox.setVisible(true);
        }
    }

    private void useBearerTokenChanged() {
        myRepository.setUseBearerTokenAuthentication(myUseBearerTokenAuthenticationCheckBox.isSelected());
        adjustSettingsForServerProperties();
    }
}
