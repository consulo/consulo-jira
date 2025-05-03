package com.intellij.jira.ui.highlighters;

import com.intellij.jira.data.JiraIssuesData;
import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class MyIssuesHighlighterFactory implements JiraIssueHighlighterFactory {

    @Override
    public JiraIssueHighlighter createHighlighter(JiraIssuesData data) {
        return new MyIssuesHighlighter(data);
    }

    @Override
    public String getId() {
        return "MY_ISSUES";
    }

    @Override
    public String getTitle() {
        return "My Issues";
    }
}
