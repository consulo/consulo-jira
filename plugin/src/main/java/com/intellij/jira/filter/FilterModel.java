package com.intellij.jira.filter;

import com.intellij.jira.data.Issues;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public abstract class FilterModel<T> {

    @NotNull
    private final Collection<Runnable> mySetFilterListeners = new ArrayList<>();

    @Nullable
    protected T myFilter;

    protected final Supplier<Issues> myIssuesGetter;

    public FilterModel(Supplier<Issues> issuesGetter) {
        myIssuesGetter = issuesGetter;
    }

    public Issues getIssues() {
        return myIssuesGetter.get();
    }

    public void setFilter(@Nullable T filter) {
        myFilter = filter;
        saveFilterToProperties(filter);
        notifyFiltersChanged();
    }

    protected void notifyFiltersChanged() {
        for (Runnable listener : mySetFilterListeners) {
            listener.run();
        }
    }

    @Nullable
    public T getFilter() {
        if (myFilter == null) {
           // myFilter = getFilterFromProperties();
        }
        return myFilter;
    }

    protected abstract void saveFilterToProperties(@Nullable T filter);

    @Nullable
    protected abstract T getFilterFromProperties();


    @Nullable
    protected abstract T createFilter(@NotNull List<String> values);

    @NotNull
    protected abstract List<String> getFilterValues(@NotNull T filter);

    public void addSetFilterListener(@NotNull Runnable runnable) {
        mySetFilterListeners.add(runnable);
    }


}
