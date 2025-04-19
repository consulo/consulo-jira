package com.intellij.jira.ui;

import com.intellij.jira.data.Issues;
import com.intellij.jira.filter.IssueFilterCollection;
import consulo.ui.ex.action.ActionGroup;
import org.jetbrains.annotations.NotNull;

import java.util.EventListener;

public interface IssuesFilterUi {

    IssueFilterCollection getFilters();

    @NotNull
    ActionGroup createActionGroup();


    void updateIssues(Issues issues);

    /**
     * Adds a listener for filters change.
     */
    void addFilterListener(@NotNull IssuesFilterUi.IssueFilterListener listener);

    interface IssueFilterListener extends EventListener {
        void onFiltersChanged();
    }

}
