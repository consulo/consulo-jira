package com.intellij.jira.data;

import com.intellij.jira.filter.IssueFilterCollection;
import consulo.disposer.Disposable;

public interface JiraVisibleIssuesRefresher extends Disposable {

    void addVisibleIssueChangeListener(VisibleIssueChangeListener listener);

    void removeVisibleIssueChangeListener(VisibleIssueChangeListener listener);

    void updateIssues(Issues newIssues);

    void onFiltersChange(IssueFilterCollection filters);

    interface VisibleIssueChangeListener {
        void onChange(Issues issues);
    }

}
