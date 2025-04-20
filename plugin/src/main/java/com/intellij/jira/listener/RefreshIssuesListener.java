package com.intellij.jira.listener;

public interface RefreshIssuesListener {

    Class<RefreshIssuesListener> TOPIC = RefreshIssuesListener.class;

    void onRefresh();

}
