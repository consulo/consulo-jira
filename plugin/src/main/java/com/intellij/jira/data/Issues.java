package com.intellij.jira.data;

import com.intellij.jira.rest.model.JiraIssue;

import java.util.ArrayList;
import java.util.List;

public class Issues {

    public static final Issues EMPTY = new Issues(new ArrayList<>());

    private final List<JiraIssue> myIssues;

    private Issues(List<JiraIssue> issues) {
        this.myIssues = issues;
    }

    public static Issues of(List<JiraIssue> issues) {
        return new Issues(issues);
    }

    public List<JiraIssue> get() {
        return myIssues;
    }

}
