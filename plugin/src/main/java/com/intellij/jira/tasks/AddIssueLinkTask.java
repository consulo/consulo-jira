package com.intellij.jira.tasks;

import com.intellij.jira.exceptions.InvalidResultException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.util.result.Result;
import consulo.application.progress.ProgressIndicator;
import consulo.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AddIssueLinkTask extends AbstractBackgroundableTask {

    private String linkType;
    private String inIssueKey;
    private String outIssueKey;


    public AddIssueLinkTask(@Nullable Project project, String issueKey, String linkType, String inIssueKey, String outIssueKey) {
        super(project, "Adding issue link", issueKey);
        this.linkType = linkType;
        this.inIssueKey = inIssueKey;
        this.outIssueKey = outIssueKey;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        JiraRestApi jiraRestApi = getJiraRestApi();

        Result result = jiraRestApi.addIssueLink(linkType, inIssueKey, outIssueKey);
        if(!result.isValid()) {
            throw new InvalidResultException("Error", "Issue comment has not been added");
        }

    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        showNotification("Jira", "Issue Link created successfully");
    }

}
