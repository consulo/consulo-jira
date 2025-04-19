package com.intellij.jira.filter.type;

import com.intellij.jira.filter.IssueFilter;
import com.intellij.jira.filter.IssueFilterCollection;
import com.intellij.jira.rest.model.JiraIssueType;
import consulo.util.lang.StringUtil;

import java.util.Collection;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_TYPE_FILTER;

public interface TypeFilter extends IssueFilter {

    @Override
    default IssueFilterCollection.FilterKey<TypeFilter> getKey() {
        return ISSUE_TYPE_FILTER;
    }

    @Override
    default String getDisplayText() {
        return StringUtil.join(getIssueTypes(), ", ");
    }

    Collection<String> getIssueTypes();

    boolean matches(JiraIssueType issueType);

}
