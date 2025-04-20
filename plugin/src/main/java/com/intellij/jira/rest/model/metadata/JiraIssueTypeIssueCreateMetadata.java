package com.intellij.jira.rest.model.metadata;

import com.google.gson.JsonObject;
import com.intellij.jira.rest.model.JiraIssueType;

public class JiraIssueTypeIssueCreateMetadata extends JiraIssueType {

    private JsonObject fields;

    public JsonObject getFields() {
        return fields;
    }
}
