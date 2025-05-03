package com.intellij.jira.ui;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import jakarta.annotation.Nonnull;

public final class JiraUiFactory {

    private JiraUiFactory() { }

    @Nonnull
    public static AbstractIssuesUi createIssuesUi(JiraIssuesData issuesData) {
        return new DefaultIssuesUi(issuesData);
    }

    @Nonnull
    public static AbstractIssuesUi createFilteredIssuesUi(JiraIssuesData issuesData, JQLSearcher searcher) {
        return new FilteredIssuesUi(issuesData, searcher);
    }

    @Nonnull
    public static JiraUi createDetailsIssueUi(String id, JiraIssuesData issuesData) {
        return new DetailsIssueUi(issuesData, id);
    }

}
