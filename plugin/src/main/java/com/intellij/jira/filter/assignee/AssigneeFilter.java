package com.intellij.jira.filter.assignee;

import com.intellij.jira.filter.IssueFilter;
import com.intellij.jira.filter.IssueFilterCollection;
import com.intellij.jira.rest.model.JiraIssue;
import consulo.util.lang.StringUtil;

import java.util.Collection;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_ASSIGNEE_FILTER;

public interface AssigneeFilter extends IssueFilter {

    @Override
    default IssueFilterCollection.FilterKey<?> getKey() {
        return ISSUE_ASSIGNEE_FILTER;
    }

    @Override
    default String getDisplayText() {
        return StringUtil.join(getUsers(), ", ");
    }

    Collection<String> getUsers();

    boolean matches(JiraIssue issue);

}
