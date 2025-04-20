package com.intellij.jira.rest.model.metadata;

import java.util.ArrayList;
import java.util.List;

public class JiraIssueCreateMetadata {

    private List<JiraProjectIssueCreateMetadata> projects;

    public JiraIssueCreateMetadata() {
        this.projects = new ArrayList<>();
    }

    public List<JiraProjectIssueCreateMetadata> getProjects() {
        return projects;
    }

}
