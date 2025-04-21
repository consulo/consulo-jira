package com.intellij.jira.components;

import com.intellij.jira.rest.model.JiraIssueUser;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.util.result.Result;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.application.ApplicationManager;
import consulo.project.Project;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static com.intellij.jira.server.JiraServerManager.JIRA_SERVER_CHANGED;

@ServiceAPI(ComponentScope.PROJECT)
@ServiceImpl
@Singleton
public class JiraUserProvider {

    private final Project myProject;
    private JiraIssueUser myCurrentUser;

    @Inject
    public JiraUserProvider(Project project) {
        myProject = project;
        project.getMessageBus().connect().subscribe(JIRA_SERVER_CHANGED, this::clearCurrentUser);
    }

    public JiraIssueUser getCurrent() {
        if (myCurrentUser == null) {
            Result currentUser = getJiraServerManager().getJiraRestApi(myProject).getCurrentUser();
            if (currentUser.isValid()) {
                myCurrentUser = (JiraIssueUser) currentUser.get();
            }
        }

        return myCurrentUser;
    }

    private JiraServerManager getJiraServerManager() {
        return ApplicationManager.getApplication().getInstance(JiraServerManager.class);
    }

    public void clearCurrentUser() {
        myCurrentUser = null;
    }

    public static JiraUserProvider getInstance(Project project) {
        return project.getInstance(JiraUserProvider.class);
    }

}
