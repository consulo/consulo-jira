package com.intellij.jira.rest.model;

public class JiraIssueTimeTracking {
    private String remainingEstimate;
    private String timeSpent;

    public JiraIssueTimeTracking() { }

    public String getRemainingEstimate() {
        return remainingEstimate;
    }
}
