package com.intellij.jira.rest;

import com.intellij.jira.rest.model.JiraGroup;

import java.util.ArrayList;
import java.util.List;

public class JiraGroupsWrapper {

    private List<JiraGroup> groups = new ArrayList<>();

    public JiraGroupsWrapper() { }

    public List<JiraGroup> getGroups() {
        return groups;
    }
}
