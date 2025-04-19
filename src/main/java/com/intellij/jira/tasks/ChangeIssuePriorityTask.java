package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import consulo.application.progress.ProgressIndicator;
import consulo.project.Project;
import org.jetbrains.annotations.NotNull;

import static com.intellij.jira.rest.model.JiraPermissionType.BROWSE_PROJECTS;
import static com.intellij.jira.rest.model.JiraPermissionType.EDIT_ISSUES;

public class ChangeIssuePriorityTask extends AbstractBackgroundableTask {

    private final String myPriorityName;

    public ChangeIssuePriorityTask(@NotNull Project project, String priorityName, String issueIdOrKey) {
        super(project, "Updating Issue Priority...", issueIdOrKey);
        this.myPriorityName = priorityName;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();
        // Check user permissions
        boolean hasPermission = jiraRestApi.userHasPermissionOnIssue(issueIdOrKey, BROWSE_PROJECTS, EDIT_ISSUES);
        if(!hasPermission){
            throw new InvalidPermissionException("Edit Issue Failed", "You don't have permission to change priority");
        }

        Result result = jiraRestApi.changeIssuePriority(myPriorityName, issueIdOrKey);
        if(!result.isValid()){
            throw new InvalidResultException("Error", "Issue priority has not been updated");
        }

    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        showNotification("Jira", "Issue priority updated");
    }

}
