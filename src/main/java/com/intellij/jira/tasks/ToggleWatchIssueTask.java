package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.rest.model.JiraIssueUser;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import consulo.application.progress.ProgressIndicator;
import consulo.project.Project;
import org.jetbrains.annotations.NotNull;

import static com.intellij.jira.rest.model.JiraPermissionType.BROWSE_PROJECTS;
import static com.intellij.jira.rest.model.JiraPermissionType.MANAGE_WATCHERS;

public class ToggleWatchIssueTask extends AbstractBackgroundableTask {

    private boolean isWatching;

    public ToggleWatchIssueTask(@NotNull Project project, String issueKey, boolean isWatching) {
        super(project, "Editing Issue watchers...", issueKey);
        this.isWatching = isWatching;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        boolean hasPermission = jiraRestApi.userHasPermissionOnIssue(issueIdOrKey, BROWSE_PROJECTS, MANAGE_WATCHERS);
        if(!hasPermission){
            throw new InvalidPermissionException("Edit Issue Failed", "You don't have permission to manage watchers");
        }

        if (isWatching) {
            Result result = jiraRestApi.getCurrentUser();
            if(result.isValid()) {
                JiraIssueUser currentUser = (JiraIssueUser) result.get();
                jiraRestApi.unwatchIssue(issueIdOrKey, currentUser.getAccountId(), currentUser.getName());
            }
        } else {
            jiraRestApi.watchIssue(issueIdOrKey);
        }

    }

}
