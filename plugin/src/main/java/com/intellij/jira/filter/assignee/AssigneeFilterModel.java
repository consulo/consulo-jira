package com.intellij.jira.filter.assignee;

import com.intellij.jira.data.Issues;
import com.intellij.jira.filter.FilterModel;
import com.intellij.jira.filter.IssueFilterCollection;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_ASSIGNEE_FILTER;

public class AssigneeFilterModel extends FilterModel<AssigneeFilter> {

    public AssigneeFilterModel(Supplier<Issues> issuesGetter, IssueFilterCollection filters) {
        super(issuesGetter);
        myFilter = filters.get(ISSUE_ASSIGNEE_FILTER);
    }

    @Override
    protected void saveFilterToProperties(@Nullable AssigneeFilter filter) {

    }

    @Override
    protected @Nullable AssigneeFilter getFilterFromProperties() {
        return null;
    }

    @Override
    protected @Nullable AssigneeFilter createFilter(@Nonnull List<String> values) {
        return new AssigneeFilterImpl(values);
    }

    @Override
    protected @Nonnull List<String> getFilterValues(@Nonnull AssigneeFilter filter) {
        return new ArrayList<>(filter.getUsers());
    }

    public AssigneeFilter getAssigneeFilter() {
        AssigneeFilter filter = getFilter();
        if (filter == null) {
            filter = new AssigneeFilterImpl();
        }

        return filter;
    }
}
