package com.intellij.jira.rest.model.metadata;

import com.intellij.jira.rest.model.JiraProject;

import java.util.List;

public class JiraProjectIssueCreateMetadata extends JiraProject {

    private List<JiraIssueTypeIssueCreateMetadata> issuetypes;

    public List<JiraIssueTypeIssueCreateMetadata> getIssuetypes() {
        return issuetypes;
    }

}
