package com.intellij.jira.util.provider;

import com.intellij.jira.rest.model.JiraIssue;

/**
 * Interface for providing issue fields
 */
public interface Provider {

    String getKey();

    String getValue(JiraIssue issue);

}
