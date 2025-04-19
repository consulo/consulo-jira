package com.intellij.jira.filter.status;

import com.intellij.jira.filter.IssueFilter;
import com.intellij.jira.filter.IssueFilterCollection;
import com.intellij.jira.rest.model.JiraIssueStatus;
import consulo.util.lang.StringUtil;

import java.util.Collection;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_STATUS_FILTER;

public interface StatusFilter extends IssueFilter {

    @Override
    default IssueFilterCollection.FilterKey<StatusFilter> getKey() {
        return ISSUE_STATUS_FILTER;
    }

    @Override
    default String getDisplayText() {
        return StringUtil.join(getIssueStatus(), ", ");
    }

    Collection<String> getIssueStatus();

    boolean matches(JiraIssueStatus status);

}
