package com.intellij.jira.ui.highlighters;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueStatus;
import com.intellij.jira.ui.JiraIssueStyleFactory;
import consulo.ui.ex.Gray;
import consulo.ui.ex.JBColor;
import jakarta.annotation.Nonnull;

public class ResolvedIssuesHighlighter implements JiraIssueHighlighter {
    public static final JBColor MERGE_COMMIT_FOREGROUND = new JBColor(Gray._128, Gray._96);

    @Override
    public @Nonnull JiraIssueStyle getStyle(@Nonnull JiraIssue issue) {
        JiraIssueStatus issueStatus = issue.getStatus();
        if (issueStatus.isDoneCategory()) {
            return JiraIssueStyleFactory.foreground(MERGE_COMMIT_FOREGROUND);
        }

        return JiraIssueStyle.DEFAULT;
    }

}
