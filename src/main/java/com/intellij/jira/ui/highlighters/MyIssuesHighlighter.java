package com.intellij.jira.ui.highlighters;

import com.intellij.jira.components.JiraUserProvider;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueUser;
import com.intellij.jira.ui.JiraIssueStyleFactory;
import consulo.project.Project;
import org.jetbrains.annotations.NotNull;

public class MyIssuesHighlighter implements JiraIssueHighlighter {

    private JiraIssuesData myData;

    public MyIssuesHighlighter(JiraIssuesData myData) {
        this.myData = myData;
    }

    @Override
    public @NotNull JiraIssueStyle getStyle(@NotNull JiraIssue issue) {
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

    public static class Factory implements JiraIssueHighlighterFactory {

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


}
