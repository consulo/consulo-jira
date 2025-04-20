package com.intellij.jira.ui.editors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.rest.model.JiraIssueUser;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.intellij.jira.util.JiraGsonUtil.createArrayNameObjects;
import static com.intellij.jira.util.JiraGsonUtil.createIdObject;
import static consulo.util.collection.ContainerUtil.getFirstItem;
import static consulo.util.lang.StringUtil.isEmpty;
import static consulo.util.lang.StringUtil.trim;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class UserSelectFieldEditor extends SelectFieldEditor<JiraIssueUser> {

    private List<JiraIssueUser> mySelectedUsers = new ArrayList<>();

    public UserSelectFieldEditor(String fieldName, Object fieldValue, boolean required) {
        this(fieldName, fieldValue, required, false);
    }

    public UserSelectFieldEditor(String fieldName, Object fieldValue, boolean required, boolean isMultiSelect) {
        super(fieldName, fieldValue, required, isMultiSelect);
        myButtonAction = new UserPickerDialogAction();
        JiraIssueUser user = getFieldValue();
        if(Objects.nonNull(user)) {
            myTextField.setText(user.getDisplayName());
            mySelectedUsers.add(user);
        }
    }

    @Override
    public JsonElement getJsonValue() {
        if(isEmpty(trim(myTextField.getText()))){
            return JsonNull.INSTANCE;
        }

        List<String> selectedUserNames = getSelectedUserNames();
        if(myIsMultiSelect){
            return createArrayNameObjects(selectedUserNames);
        }

        return createIdObject(getFirstItem(selectedUserNames));
    }

    @Override
    public JiraIssueUser getFieldValue() {
        if (Objects.isNull(myFieldValue)) {
            return null;
        }

        return ((JiraIssueUser) myFieldValue);
    }

    private List<String> getSelectedUserNames() {
        return mySelectedUsers.stream().map(JiraIssueUser::getAccountId).collect(toList());
    }

    private class UserPickerDialogAction extends PickerDialogAction {

        public UserPickerDialogAction() {
            super();
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            super.actionPerformed(e);
            if(nonNull(myJiraRestApi)){
                List<JiraIssueUser> users;
                String issueKey = e.getData(JiraDataKeys.ISSUE_KEY);
                if (isNull(issueKey)) {
                    String projectKey = e.getData(JiraDataKeys.PROJECT_KEY);
                    users = myJiraRestApi.getProjectAssignableUsers(projectKey);
                } else {
                    users = myJiraRestApi.getIssueAssignableUsers(issueKey);
                }

                UserPickerDialog dialog = new UserPickerDialog(myProject, users, getFieldValue());
                dialog.show();
            }

        }
    }

    class UserPickerDialog extends PickerDialog<JiraIssueUser> {

        public UserPickerDialog(@Nullable Project project, List<JiraIssueUser> items, JiraIssueUser selectedUser) {
            super(project, "Users", items, Collections.singletonList(selectedUser));
        }

        @Override
        protected void doOKAction() {
            mySelectedUsers = myList.getSelectedValuesList();
            myTextField.setText(mySelectedUsers.isEmpty() ? "" : mySelectedUsers.stream().map(JiraIssueUser::getDisplayName).collect(Collectors.joining(", ")));

            super.doOKAction();
        }
    }

}
