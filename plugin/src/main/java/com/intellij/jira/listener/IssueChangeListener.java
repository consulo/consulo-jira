package com.intellij.jira.listener;

import com.intellij.jira.rest.model.JiraIssue;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.TopicAPI;
import jakarta.annotation.Nonnull;

@TopicAPI(ComponentScope.PROJECT)
public interface IssueChangeListener {
    Class<IssueChangeListener> TOPIC = IssueChangeListener.class;

    void onChange(@Nonnull JiraIssue issue);
}
