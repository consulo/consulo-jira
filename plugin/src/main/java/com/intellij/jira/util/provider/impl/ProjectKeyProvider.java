package com.intellij.jira.util.provider.impl;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.util.provider.Provider;
import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class ProjectKeyProvider implements Provider {

    @Override
    public String getKey() {
        return "projectKey";
    }

    @Override
    public String getValue(JiraIssue issue) {
        return issue.getProject().getKey();
    }
}
