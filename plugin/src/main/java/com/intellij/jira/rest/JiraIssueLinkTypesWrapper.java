package com.intellij.jira.rest;

import com.intellij.jira.rest.model.JiraIssueLinkType;

import java.util.ArrayList;
import java.util.List;

public class JiraIssueLinkTypesWrapper {

    private List<JiraIssueLinkType> issueLinkTypes = new ArrayList<>();

    public JiraIssueLinkTypesWrapper() { }

    public List<JiraIssueLinkType> getIssueLinkTypes() {
        return issueLinkTypes;
    }
}
