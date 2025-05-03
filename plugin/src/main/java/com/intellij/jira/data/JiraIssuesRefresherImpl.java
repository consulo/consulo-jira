package com.intellij.jira.data;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraServerManager;
import consulo.application.progress.ProgressIndicator;
import consulo.application.progress.ProgressManager;
import consulo.application.progress.Task;
import consulo.component.ProcessCanceledException;
import consulo.disposer.Disposable;
import consulo.jira.impl.SingleTaskController;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JiraIssuesRefresherImpl implements JiraIssuesRefresher, Disposable {

    private final Project myProject;
    private final JiraProgress myProgress;
    private Issues myIssues = Issues.EMPTY;

    private final SingleTaskController<RefreshRequest, Issues> mySingleTaskController;

    public JiraIssuesRefresherImpl(@Nonnull Project project, @Nonnull JiraProgress progress, @Nonnull Consumer<? super Issues> issuesUpdateHandler) {
        myProject = project;
        myProgress = progress;

        mySingleTaskController = new SingleTaskController<>("refresh", this, issues -> {
            myIssues = issues;
            issuesUpdateHandler.accept(issues);
        }) {
            @Override
            protected @Nonnull SingleTask startNewBackgroundTask() {
                return JiraIssuesRefresherImpl.this.startNewBackgroundTask(new MyRefreshTask());
            }
        };
    }


    protected SingleTaskController.SingleTask startNewBackgroundTask(@Nonnull final Task.Backgroundable refreshTask) {
        ProgressIndicator indicator = myProgress.createProgressIndicator();
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(refreshTask, indicator);
        return new SingleTaskController.SingleTaskImpl(indicator);
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

    @Nonnull
    private JiraServerManager getJiraServerManager() {
        return JiraServerManager.getInstance();
    }


    private static class RefreshRequest {
        private final String jql;

        RefreshRequest(@Nonnull String jql) {
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
        public void run(@Nonnull ProgressIndicator indicator) {
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
                issues.addAll(getJiraServerManager().getJiraRestApi((Project) myProject).getIssues(jql));
            }

            return Issues.of(issues);
        }
    }
}
