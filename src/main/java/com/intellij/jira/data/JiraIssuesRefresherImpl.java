package com.intellij.jira.data;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.CoreProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.data.SingleTaskController;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JiraIssuesRefresherImpl implements JiraIssuesRefresher, Disposable {

    private final Project myProject;
    private final JiraProgress myProgress;
    private Issues myIssues = Issues.EMPTY;

    private final SingleTaskController<RefreshRequest, Issues> mySingleTaskController;

    public JiraIssuesRefresherImpl(@NotNull Project project, @NotNull JiraProgress progress, @NotNull Consumer<? super Issues> issuesUpdateHandler) {
        myProject = project;
        myProgress = progress;

        mySingleTaskController = new SingleTaskController<>("refresh", this, issues -> {
            myIssues = issues;
            issuesUpdateHandler.accept(issues);
        }) {
            @Override
            protected @NotNull SingleTask startNewBackgroundTask() {
                return JiraIssuesRefresherImpl.this.startNewBackgroundTask(new MyRefreshTask());
            }
        };
    }


    protected SingleTaskController.SingleTask startNewBackgroundTask(@NotNull final Task.Backgroundable refreshTask) {
        ProgressIndicator indicator = myProgress.createProgressIndicator();
        Future<?> future = ((CoreProgressManager) ProgressManager.getInstance()).runProcessWithProgressAsynchronously(refreshTask, indicator,
                null);
        return new SingleTaskController.SingleTaskImpl(future, indicator);
    }

    public Issues getCurrentIssues() {
        return myIssues;
    }

    @Override
    public void getIssues(String jql) {
        mySingleTaskController.request(new RefreshRequest(jql));
    }

    @Override
    public void dispose() {

    }

    public JiraProgress getProgress() {
        return myProgress;
    }

    @NotNull
    private JiraServerManager getJiraServerManager() {
        return JiraServerManager.getInstance();
    }


    private static class RefreshRequest {
        private final String jql;

        RefreshRequest(@NotNull String jql) {
            this.jql = jql;
        }

        public String getJql() {
            return jql;
        }
    }

    private class MyRefreshTask extends Task.Backgroundable {

        public MyRefreshTask() {
            super(JiraIssuesRefresherImpl.this.myProject, "Fetching");
        }

        @Override
        public void run(@NotNull ProgressIndicator indicator) {
            indicator.setIndeterminate(true);
            Issues issues = myIssues;
            while (true) {
                List<RefreshRequest> requests = mySingleTaskController.popRequests();
                List<String> jqls = requests.stream().map(JiraIssuesRefresherImpl.RefreshRequest::getJql).collect(Collectors.toList());

                if (jqls.isEmpty()) {
                    mySingleTaskController.taskCompleted(issues);
                    break;
                }

                try {
                    issues = doRefresh(jqls);
                    mySingleTaskController.taskCompleted(issues);
                } catch (ProcessCanceledException e) {
                    mySingleTaskController.taskCompleted(null);
                    throw e;
                }
            }
        }

        private Issues doRefresh(List<String> jqls) {
            List<JiraIssue> issues = new ArrayList<>();
            for (String jql : jqls) {
                issues.addAll(getJiraServerManager().getJiraRestApi(myProject).getIssues(jql));
            }

            return Issues.of(issues);
        }
    }
}
