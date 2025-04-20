package com.intellij.jira.filter;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

public interface IssueFilterCollection {

    Collection<IssueFilter> getFilters();

    <T extends IssueFilter> T get(FilterKey<T> key);

    class FilterKey<T extends IssueFilter> {
        @NotNull
        private final String myName;

        public FilterKey(@NotNull String name) {
            myName = name;
        }

        @NotNull
        public String getName() {
            return myName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IssueFilterCollection.FilterKey<?> key = (IssueFilterCollection.FilterKey<?>)o;
            return Objects.equals(myName, key.myName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(myName);
        }

        public static <T extends IssueFilter> IssueFilterCollection.FilterKey<T> create(@NonNls @NotNull String name) {
            return new IssueFilterCollection.FilterKey<>(name);
        }

        @Override
        @NonNls
        public String toString() {
            return myName + " filter";
        }
    }

}
