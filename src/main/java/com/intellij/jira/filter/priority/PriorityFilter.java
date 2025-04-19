package com.intellij.jira.filter.priority;

import com.intellij.jira.filter.IssueFilter;
import com.intellij.jira.filter.IssueFilterCollection;
import com.intellij.jira.rest.model.JiraIssuePriority;
import consulo.util.lang.StringUtil;

import java.util.Collection;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_PRIORITY_FILTER;

public interface PriorityFilter extends IssueFilter {

    @Override
    default IssueFilterCollection.FilterKey<?> getKey() {
        return ISSUE_PRIORITY_FILTER;
    }

    @Override
    default String getDisplayText() {
        return StringUtil.join(getPriorities(), ", ");
    }

    Collection<String> getPriorities();

    boolean matches(JiraIssuePriority priority);

}
