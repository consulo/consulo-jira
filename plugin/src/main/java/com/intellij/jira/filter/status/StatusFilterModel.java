package com.intellij.jira.filter.status;

import com.intellij.jira.data.Issues;
import com.intellij.jira.filter.FilterModel;
import com.intellij.jira.filter.IssueFilterCollection;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_STATUS_FILTER;

public class StatusFilterModel extends FilterModel<StatusFilter> {

    public StatusFilterModel(Supplier<Issues> issuesGetter, IssueFilterCollection filters) {
        super(issuesGetter);
        myFilter = filters.get(ISSUE_STATUS_FILTER);
    }

    @Override
    protected void saveFilterToProperties(@Nullable StatusFilter filter) {

    }

    @Override
    protected @Nullable StatusFilter getFilterFromProperties() {
        return null;
    }

    @Override
    protected @Nullable StatusFilter createFilter(@Nonnull List<String> values) {
        return new StatusFilterImpl(values);
    }

    @Override
    protected @Nonnull List<String> getFilterValues(@Nonnull StatusFilter filter) {
        return new ArrayList<>(filter.getIssueStatus());
    }

    public StatusFilter getIssueTypeFilter() {
        StatusFilter filter = getFilter();
        if (filter == null) {
            filter = new StatusFilterImpl();
        }

        return filter;
    }
}
