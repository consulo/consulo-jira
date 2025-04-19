package com.intellij.jira.tasks;

import com.intellij.jira.components.JiraNotificationManager;
import com.intellij.jira.exceptions.JiraServerConfigurationNotFoundException;
import com.intellij.jira.listener.IssueChangeListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.util.result.Result;
import consulo.application.ApplicationManager;
import consulo.application.progress.Task;
import consulo.project.Project;
import consulo.project.ui.notification.Notifications;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public abstract class AbstractBackgroundableTask extends Task.Backgroundable {

    protected String issueIdOrKey;
    protected JiraServerManager jiraServerManager;

    public AbstractBackgroundableTask(@NotNull Project project, @NotNull String title, String issueIdOrKey) {
        super(project, title, false, ALWAYS_BACKGROUND);
        this.issueIdOrKey = issueIdOrKey;
        this.jiraServerManager = ApplicationManager.getApplication().getInstance(JiraServerManager.class);
    }

    @NotNull
    public JiraRestApi getJiraRestApi() throws JiraServerConfigurationNotFoundException{
        JiraRestApi jiraRestApi = jiraServerManager.getJiraRestApi((Project) myProject);
        if(isNull(jiraRestApi)) {
            throw new JiraServerConfigurationNotFoundException();
        }

        return jiraRestApi;
    }

    @Override
    public void onThrowable(@NotNull Throwable error) {
        String content = nonNull(error.getCause()) ? error.getCause().getMessage() : "";
        Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotificationError(error.getMessage(), content));
    }

    @Override
    public void onSuccess() {
        Result issueResult = getJiraRestApi().getIssue(issueIdOrKey);
        JiraIssue issue = (JiraIssue) issueResult.get();

        if (issue != null) {
            myProject.getMessageBus().syncPublisher(IssueChangeListener.TOPIC).onChange(issue);
        }
    }

    public void showNotification(String title, String content){
        Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotification(title, content));
    }

}
