package com.intellij.jira.rest;

import com.intellij.jira.rest.model.JiraIssueComment;

import java.util.ArrayList;
import java.util.List;

public class JiraIssueCommentsWrapper extends JiraResponseWrapper {

    private List<JiraIssueComment> comments = new ArrayList<>();

    protected JiraIssueCommentsWrapper() { }

    public List<JiraIssueComment> getComments() {
        return comments;
    }
}
