package com.intellij.jira.ui.editors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.actions.AddIssueLinkDialogAction;
import com.intellij.jira.rest.model.JiraIssueLinkType;
import com.intellij.jira.rest.model.JiraIssueLinkTypeInfo;
import com.intellij.jira.ui.dialog.AddIssueLinkDialog;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
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
    private String projectKey;

    public LinkedIssueFieldEditor(String issueKey, String fieldName, boolean required, String projectKey) {
        super(issueKey, fieldName, null, required);
        this.projectKey = projectKey;
    }

    @Override
    public String getFieldValue() {
        return null;
    }

    @Override
    public JComponent createPanel() {
        this.myButton.setIcon(AllIcons.Ide.UpDown);
        this.myButton.addActionListener(e -> {
            InputEvent inputEvent = e.getSource() instanceof InputEvent ? (InputEvent)e.getSource() : null;
            MyAddIssueLinkDialogAction myAction = new MyAddIssueLinkDialogAction();
            myAction.actionPerformed(AnActionEvent.createFromAnAction(myAction, inputEvent, ActionPlaces.UNKNOWN, DataManager.getInstance().getDataContext(myTextField)));
        });

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(myLabel, myPanel)
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
            return new ValidationInfo(myLabel.getMyLabelText() + " is required.");
        }

        return null;
    }

    @Override
    public @Nullable Object getData(@NotNull String dataId) {
        if (JiraDataKeys.PROJECT_KEY.is(dataId)) {
            return projectKey;
        }

        return super.getData(dataId);
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
