package com.intellij.jira.listener;

import com.intellij.jira.rest.model.JiraCreatedIssue;
import org.jetbrains.annotations.NotNull;

public interface IssueCreatedListener {

    Class<IssueCreatedListener> TOPIC = IssueCreatedListener.class;

    void onCreated(@NotNull JiraCreatedIssue createdIssue);

}
