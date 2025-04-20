package com.intellij.jira.rest;

import com.intellij.jira.rest.model.JiraIssueTransition;

import java.util.ArrayList;
import java.util.List;

public class JiraIssueTransitionsWrapper <T extends JiraIssueTransition> extends JiraResponseWrapper {

    private List<T> transitions = new ArrayList<>();

    public JiraIssueTransitionsWrapper() { }

    public List<T> getTransitions() {
        return transitions;
    }
}
