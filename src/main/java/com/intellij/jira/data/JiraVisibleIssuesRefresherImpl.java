package com.intellij.jira.data;

import com.intellij.jira.filter.IssueFilterCollection;
import com.intellij.jira.filter.IssueFilterer;
import consulo.application.progress.ProgressIndicator;
import consulo.application.progress.ProgressManager;
import consulo.application.progress.Task;
import consulo.component.ProcessCanceledException;
import consulo.project.Project;
import consulo.util.collection.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class JiraVisibleIssuesRefresherImpl implements JiraVisibleIssuesRefresher {

    private final Project myProject;
    private IssueFilterCollection myFilters;
    private final IssueFilterer myIssueFilterer;
    private final JiraIssuesData myIssueData;

    private Issues myIssues;
    private Issues myFilteredIssues;

    private final SingleTaskController<Request, Issues> mySingleTaskController;
    private List<VisibleIssueChangeListener> myVisibleIssueChangeListeners = new ArrayList<>();

    public JiraVisibleIssuesRefresherImpl(@NotNull JiraIssuesData issuesData, @NotNull Issues initialIssues, JiraProgress progress, IssueFilterCollection filters, IssueFilterer issueFilterer) {
        myProject = issuesData.getProject();
        myIssueData = issuesData;
        myFilters = filters;
        myIssueFilterer = issueFilterer;
        myIssues = initialIssues;
        myFilteredIssues = Issues.EMPTY;

        mySingleTaskController = new SingleTaskController<>("visible", this, issues -> {
            myFilteredIssues = issues;
            for (VisibleIssueChangeListener listener : myVisibleIssueChangeListeners) {
                listener.onChange(issues);
            }
        }) {
            @Override
            protected @NotNull SingleTask startNewBackgroundTask() {
                ProgressIndicator indicator = progress.createProgressIndicator();
                Future<?> future = ((CoreProgressManager) ProgressManager.getInstance()).runProcessWithProgressAsynchronously(new MyTask(myProject), indicator,
                        null);
                return new SingleTaskController.SingleTaskImpl(future, indicator);
            }
        };
    }

    @Override
    public void addVisibleIssueChangeListener(VisibleIssueChangeListener listener) {
        myVisibleIssueChangeListeners.add(listener);
    }

    @Override
    public void removeVisibleIssueChangeListener(VisibleIssueChangeListener listener) {
        myVisibleIssueChangeListeners.remove(listener);
    }

    @Override
    public void updateIssues(Issues newIssues) {
        myIssues = newIssues;
        mySingleTaskController.request(new FilterRequest(myFilters));
    }

    @Override
    public void onFiltersChange(IssueFilterCollection filters) {
        myFilters = filters;
        mySingleTaskController.request(new FilterRequest(filters));
    }

    @Override
    public void dispose() {
        myVisibleIssueChangeListeners.clear();
    }

    private interface Request {
    }

    private static class FilterRequest implements Request {

        private final IssueFilterCollection myFilters;

        public FilterRequest(IssueFilterCollection filters) {
            myFilters = filters;
        }

    }

    private class MyTask extends Task.Backgroundable {

        public MyTask(@NotNull Project project) {
            super(project, "Applying filters");
        }

        @Override
        public void run(@NotNull ProgressIndicator indicator) {
            indicator.setIndeterminate(true);
            Issues issues = myIssues;
            List<Request> requests = mySingleTaskController.peekRequests();
            if (!requests.isEmpty()) {


                try {
                    issues = doRun(issues, requests);
                    mySingleTaskController.removeRequests(requests);
                } catch (ProcessCanceledException e) {
                    mySingleTaskController.taskCompleted(null);
                    throw e;
                }

                mySingleTaskController.taskCompleted(issues);
            }
        }

        private Issues doRun(Issues issues, List<Request> requests) {
            FilterRequest filterRequest = ContainerUtil.findLastInstance(requests, FilterRequest.class);

            if (filterRequest != null) {
                issues = myIssueFilterer.filter(issues, filterRequest.myFilters);
            }

            return issues;
        }
    }


}
