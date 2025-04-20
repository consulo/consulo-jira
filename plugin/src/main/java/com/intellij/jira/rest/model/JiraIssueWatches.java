package com.intellij.jira.rest.model;

public class JiraIssueWatches {

    private String self;
    private int watchCount;
    private boolean isWatching;

    public JiraIssueWatches() { }

    public int getWatchCount() {
        return watchCount;
    }

    public boolean isWatching() {
        return isWatching;
    }
}
