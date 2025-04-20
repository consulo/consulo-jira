package com.intellij.jira.listener;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.NotNull;

public interface IssueChangeListener {

    Class<IssueChangeListener> TOPIC = Topic.create("Issue has changed", IssueChangeListener.class);

    void onChange(@NotNull JiraIssue issue);

}
