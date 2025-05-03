package com.intellij.jira.filter.priority;


import com.intellij.jira.data.Issues;
import com.intellij.jira.filter.FilterModel;
import com.intellij.jira.filter.IssueFilterCollection;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_PRIORITY_FILTER;

public class PriorityFilterModel extends FilterModel<PriorityFilter> {


    public PriorityFilterModel(Supplier<Issues> issuesGetter, IssueFilterCollection filters) {
        super(issuesGetter);
        myFilter = filters.get(ISSUE_PRIORITY_FILTER);
    }

    @Override
    protected void saveFilterToProperties(@Nullable PriorityFilter filter) {

    }

    @Override
    protected @Nullable PriorityFilter getFilterFromProperties() {
        return null;
    }

    @Override
    protected @Nullable PriorityFilter createFilter(@Nonnull List<String> values) {
        return new PriorityFilterImpl(values);
    }

    @Override
    protected @Nonnull List<String> getFilterValues(@Nonnull PriorityFilter filter) {
        return new ArrayList<>(filter.getPriorities());
    }

    public PriorityFilter getPriorityFilter() {
        PriorityFilter filter = getFilter();
        if (filter == null) {
            filter = new PriorityFilterImpl();
        }

        return filter;
    }
}
