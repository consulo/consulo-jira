package com.intellij.jira.ui.highlighters;

import com.intellij.jira.data.JiraIssuesData;

public interface JiraIssueHighlighterFactory {

    JiraIssueHighlighter createHighlighter(JiraIssuesData data);

    String getId();

    String getTitle();
}
