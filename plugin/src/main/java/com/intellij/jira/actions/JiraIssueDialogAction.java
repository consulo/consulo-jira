package com.intellij.jira.actions;

import com.intellij.jira.components.JiraNotificationManager;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import consulo.application.ApplicationManager;
import consulo.project.Project;
import consulo.project.ui.notification.Notifications;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import jakarta.annotation.Nonnull;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public abstract class JiraIssueDialogAction extends AnAction {

    public JiraIssueDialogAction(@Nonnull ActionProperties actionProperties) {
        super(actionProperties.getText(), actionProperties.getDescription(), actionProperties.getIcon());
    }

    @Override
    public void actionPerformed(@Nonnull AnActionEvent event) {
        try{
            Project project = event.getData(Project.KEY);
            if(isNull(project)){
                return;
            }

            JiraServerManager manager = ApplicationManager.getApplication().getInstance(JiraServerManager.class);
            JiraRestApi jiraRestApi = manager.getJiraRestApi(project);
            if(isNull(jiraRestApi)) {
                return;
            }

            onClick(event, project, jiraRestApi);

        } catch (Throwable error){
            onError(error);
        }
    }

    public abstract void onClick(@Nonnull AnActionEvent e, @Nonnull Project project, @Nonnull JiraRestApi jiraRestApi);

    public void onError(@Nonnull Throwable error){
        String content = nonNull(error.getCause()) ? error.getCause().getMessage() : "";
        Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotificationError(error.getMessage(), content));
    }

}
