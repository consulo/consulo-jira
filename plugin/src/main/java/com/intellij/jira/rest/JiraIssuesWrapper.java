package com.intellij.jira.rest;

import com.intellij.jira.rest.model.JiraIssue;

import java.util.ArrayList;
import java.util.List;

public class JiraIssuesWrapper<T extends JiraIssue> extends JiraResponseWrapper {

    private List<T> issues = new ArrayList<>();

    public JiraIssuesWrapper() { }

    public List<T> getIssues() {
        return issues;
    }
}
