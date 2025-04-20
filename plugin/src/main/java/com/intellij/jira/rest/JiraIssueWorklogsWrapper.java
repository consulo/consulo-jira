package com.intellij.jira.rest;

import com.intellij.jira.rest.model.JiraIssueWorklog;

import java.util.ArrayList;
import java.util.List;

public class JiraIssueWorklogsWrapper extends JiraResponseWrapper {

    private List<JiraIssueWorklog> worklogs = new ArrayList<>();

    protected JiraIssueWorklogsWrapper() { }

    public List<JiraIssueWorklog> getWorklogs() {
        return worklogs;
    }
}
