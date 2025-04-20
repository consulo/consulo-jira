package com.intellij.jira.util.provider.impl;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.util.JiraIssueField;
import com.intellij.jira.util.provider.Provider;

public class IssueKeyProvider implements Provider {

    @Override
    public String getKey() {
        return JiraIssueField.KEY;
    }

    @Override
    public String getValue(JiraIssue issue) {
        return issue.getKey();
    }

}
