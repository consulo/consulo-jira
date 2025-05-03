package com.intellij.jira.util.provider;

import com.intellij.jira.rest.model.JiraIssue;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;

/**
 * Interface for providing issue fields
 */
@ExtensionAPI(ComponentScope.APPLICATION)
public interface Provider {

    String getKey();

    String getValue(JiraIssue issue);
}
