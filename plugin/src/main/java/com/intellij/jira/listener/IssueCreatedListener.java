package com.intellij.jira.listener;

import com.intellij.jira.rest.model.JiraCreatedIssue;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.TopicAPI;
import jakarta.annotation.Nonnull;

@TopicAPI(ComponentScope.PROJECT)
public interface IssueCreatedListener {

    Class<IssueCreatedListener> TOPIC = IssueCreatedListener.class;

    void onCreated(@Nonnull JiraCreatedIssue createdIssue);

}
