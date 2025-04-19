package com.intellij.jira.ui.editors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.intellij.jira.actions.AddIssueLinkDialogAction;
import com.intellij.jira.rest.model.JiraIssueLinkType;
import com.intellij.jira.rest.model.JiraIssueLinkTypeInfo;
import com.intellij.jira.ui.dialog.AddIssueLinkDialog;
import consulo.application.AllIcons;
import consulo.dataContext.DataManager;
import consulo.project.Project;
import consulo.ui.ex.action.ActionPlaces;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.ValidationInfo;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.util.List;

import static com.intellij.jira.util.JiraGsonUtil.createNameObject;
import static com.intellij.jira.util.JiraGsonUtil.createObject;
import static com.intellij.jira.util.JiraIssueField.KEY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class LinkedIssueFieldEditor extends AbstractFieldEditor<String> {

    protected JPanel myPanel;
    protected JTextField myTextField;
    protected JButton myButton;

    private JiraIssueLinkTypeInfo mySelectedLinkType;
    private String mySelectedIssue;

    public LinkedIssueFieldEditor(String fieldName, boolean required) {
        super(fieldName, null, required);
    }

    @Override
    public String getFieldValue() {
        return null;
    }

    @Override
    public JComponent createPanel() {
        myButton.setIcon(AllIcons.Ide.UpDown);
        myButton.addActionListener(e -> {
            InputEvent inputEvent = e.getSource() instanceof InputEvent ? (InputEvent)e.getSource() : null;
            MyAddIssueLinkDialogAction action = new MyAddIssueLinkDialogAction();
            action.actionPerformed(AnActionEvent.createFromAnAction(action, inputEvent, ActionPlaces.TOOLBAR, DataManager.getInstance().getDataContext(myTextField)));
        });

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(myLabel, myPanel, true)
                .getPanel();
    }

    @Override
    public JsonElement getJsonValue() {
        if(isNull(mySelectedLinkType)){
            return JsonNull.INSTANCE;
        }

        JsonArray array = new JsonArray();
        JsonObject rootObject = new JsonObject();
        JsonObject addObject = new JsonObject();
        addObject.add("type", createNameObject(mySelectedLinkType.getName()));
        addObject.add(mySelectedLinkType.isInward() ? "inwardIssue" : "outwardIssue", createObject(KEY, mySelectedIssue));


        rootObject.add("add", addObject);
        array.add(rootObject);
        return array;
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if(isRequired() && StringUtil.isEmpty(myTextField.getText())){
            return new ValidationInfo(myLabel.getText() + " is required.");
        }

        return null;
    }

    private class MyAddIssueLinkDialogAction extends AddIssueLinkDialogAction {

        @Override
        public void openIssueLinkDialog(Project project, List<JiraIssueLinkType> issueLinkTypes, List<String> issues, String issueKey) {
            MyAddIssueLinkDialog dialog = new MyAddIssueLinkDialog(project, issueLinkTypes, issues, issueKey);
            dialog.show();
        }
    }

    private class MyAddIssueLinkDialog extends AddIssueLinkDialog {

        public MyAddIssueLinkDialog(@Nullable Project project, List<JiraIssueLinkType> linkTypes, List<String> issuesKey, String issueKey) {
            super(project, linkTypes, issuesKey, issueKey);
        }

        @Override
        protected void doOKAction() {
            JiraIssueLinkTypeInfo selectedType = (JiraIssueLinkTypeInfo) linkTypesCB.getSelectedItem();
            String selectedIssue = (String) issueKeysCB.getSelectedItem();
            mySelectedLinkType = selectedType;
            mySelectedIssue = selectedIssue;
            if(nonNull(selectedType) && nonNull(selectedIssue)){
                myTextField.setText(selectedType.getDescription() + " " + selectedIssue);
            }

            close(0);
        }
    }

}
