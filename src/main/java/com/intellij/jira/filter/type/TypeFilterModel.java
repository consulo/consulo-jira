package com.intellij.jira.filter.type;

import com.intellij.jira.data.Issues;
import com.intellij.jira.filter.FilterModel;
import com.intellij.jira.filter.IssueFilterCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_TYPE_FILTER;

public class TypeFilterModel extends FilterModel<TypeFilter> {

    public TypeFilterModel(Supplier<Issues> issuesGetter, IssueFilterCollection filters) {
        super(issuesGetter);
        myFilter = filters.get(ISSUE_TYPE_FILTER);
    }

    @Override
    protected void saveFilterToProperties(@Nullable TypeFilter filter) {

    }

    @Override
    protected @Nullable TypeFilter getFilterFromProperties() {
        return null;
    }

    @Nullable
    @Override
    protected TypeFilter createFilter(@NotNull List<String> values) {
        return new TypeFilterImpl(values);
    }

    @NotNull
    @Override
    protected List<String> getFilterValues(@NotNull TypeFilter filter) {
        return new ArrayList<>(filter.getIssueTypes());
    }

    public TypeFilter getIssueTypeFilter() {
        TypeFilter filter = getFilter();
        if (filter == null) {
            filter = new TypeFilterImpl();
        }

        return filter;
    }

}
