package com.intellij.jira.ui.highlighters;

import com.intellij.jira.components.JiraUserProvider;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueUser;
import com.intellij.jira.ui.JiraIssueStyleFactory;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

public class MyIssuesHighlighter implements JiraIssueHighlighter {

    private JiraIssuesData myData;

    public MyIssuesHighlighter(JiraIssuesData myData) {
        this.myData = myData;
    }

    @Override
    public @Nonnull JiraIssueStyle getStyle(@Nonnull JiraIssue issue) {
        Project project = myData.getProject();

        JiraIssueUser currentUser = JiraUserProvider.getInstance(project).getCurrent();
        JiraIssueUser assigneeUser = issue.getAssignee();

        if (currentUser != null
                && assigneeUser != null
                && currentUser.getEmailAddress() != null
                && currentUser.getEmailAddress().equals(assigneeUser.getEmailAddress())) {
            return JiraIssueStyleFactory.bold();
        }

        return JiraIssueStyle.DEFAULT;
    }


}
