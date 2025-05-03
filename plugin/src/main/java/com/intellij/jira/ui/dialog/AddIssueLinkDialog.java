package com.intellij.jira.ui.dialog;

import com.intellij.jira.rest.model.JiraIssueLinkType;
import com.intellij.jira.rest.model.JiraIssueLinkTypeInfo;
import com.intellij.jira.tasks.AddIssueLinkTask;
import com.intellij.jira.ui.model.JiraIssueLinkTypeInfoListModel;
import com.intellij.jira.util.JiraLabelUtil;
import consulo.project.Project;
import consulo.ui.ex.awt.CollectionComboBoxModel;
import consulo.ui.ex.awt.ComboBox;
import consulo.ui.ex.awt.DialogWrapper;
import consulo.ui.ex.awt.FormBuilder;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.util.List;

import static java.util.Objects.nonNull;

public class AddIssueLinkDialog extends DialogWrapper {

    private Project project;
    protected String issueKey;

    protected ComboBox<JiraIssueLinkTypeInfo> linkTypesCB;
    protected ComboBox<String> issueKeysCB;


    public AddIssueLinkDialog(@Nullable Project project, List<JiraIssueLinkType> linkTypes, List<String> issuesKey, String issueKey) {
        super(project, false);

        JiraIssueLinkTypeInfoListModel model = new JiraIssueLinkTypeInfoListModel(linkTypes);

        linkTypesCB = new ComboBox<>(new CollectionComboBoxModel<>(model.getIssueLinkTypes()), 300);
        issueKeysCB = new ComboBox<>(new CollectionComboBoxModel<>(issuesKey), 300);

        this.project = project;
        this.issueKey = issueKey;

        setTitle("Issue Link");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(JiraLabelUtil.createLabel("This issue: "), linkTypesCB)
                .addLabeledComponent(JiraLabelUtil.createLabel("Issue: "), issueKeysCB)
                .getPanel();
    }

    @Override
    protected void doOKAction() {
        JiraIssueLinkTypeInfo selectedType = (JiraIssueLinkTypeInfo) linkTypesCB.getSelectedItem();
        String selectedIssue = (String) issueKeysCB.getSelectedItem();
        if(nonNull(selectedType) && nonNull(selectedIssue)){
            String inIssueKey = selectedType.isInward() ? selectedIssue : issueKey;
            String outIssueKey = selectedType.isInward() ? issueKey : selectedIssue;

            new AddIssueLinkTask(project, issueKey, selectedType.getName(), inIssueKey, outIssueKey).queue();
        }

        super.doOKAction();
    }

}
