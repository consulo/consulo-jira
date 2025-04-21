package com.intellij.jira.listener;

import com.intellij.jira.rest.model.JiraIssue;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.TopicAPI;
import org.jetbrains.annotations.NotNull;

@TopicAPI(ComponentScope.PROJECT)
public interface IssueChangeListener {
    Class<IssueChangeListener> TOPIC = IssueChangeListener.class;

    void onChange(@NotNull JiraIssue issue);
}
