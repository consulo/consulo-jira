package com.intellij.jira.rest.model;

public class JiraIssueComponent {
    private String self;
    private String id;
    private String name;

    public JiraIssueComponent() { }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
