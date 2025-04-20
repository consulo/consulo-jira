package com.intellij.jira.filter;

import com.intellij.jira.filter.assignee.AssigneeFilter;
import com.intellij.jira.filter.priority.PriorityFilter;
import com.intellij.jira.filter.status.StatusFilter;
import com.intellij.jira.filter.type.TypeFilter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IssueFilterCollectionImpl implements IssueFilterCollection {

    public static final FilterKey<TypeFilter> ISSUE_TYPE_FILTER = FilterKey.create("Issue Type Filter");
    public static final FilterKey<StatusFilter> ISSUE_STATUS_FILTER = FilterKey.create("Issue Status Filter");
    public static final FilterKey<PriorityFilter> ISSUE_PRIORITY_FILTER = FilterKey.create("Issue Priority Filter");
    public static final FilterKey<AssigneeFilter> ISSUE_ASSIGNEE_FILTER = FilterKey.create("Issue Assignee Filter");

    private final Map<FilterKey, IssueFilter> myFilters = new HashMap<>();

    public IssueFilterCollectionImpl(Collection<IssueFilter> filters) {
        filters.forEach(filter -> myFilters.put(filter.getKey(), filter));
    }

    @Override
    public Collection<IssueFilter> getFilters() {
        return myFilters.values();
    }

    @Override
    public <T extends IssueFilter> T get(FilterKey<T> key) {
        return (T) myFilters.get(key);
    }
}
