package com.intellij.jira.ui.highlighters;

import com.intellij.jira.data.JiraIssuesData;
import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class ResolvedIssuesHighlighterFactory implements JiraIssueHighlighterFactory {

    @Override
    public JiraIssueHighlighter createHighlighter(JiraIssuesData data) {
        return new ResolvedIssuesHighlighter();
    }

    @Override
    public String getId() {
        return "RESOLVED_ISSUES";
    }

    @Override
    public String getTitle() {
        return "Resolved Issues";
    }
}
