package com.intellij.jira.ui.highlighters;

import com.intellij.jira.data.JiraIssuesData;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;

@ExtensionAPI(ComponentScope.APPLICATION)
public interface JiraIssueHighlighterFactory {

    JiraIssueHighlighter createHighlighter(JiraIssuesData data);

    String getId();

    String getTitle();
}
