package com.intellij.jira.server;

import com.intellij.jira.rest.client.JiraRestTemplate;
import com.intellij.jira.server.auth.AuthType;
import com.intellij.jira.util.SimpleSelectableList;
import com.intellij.tasks.jira.JiraRepository;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.application.ApplicationManager;
import consulo.project.Project;
import consulo.task.TaskManager;
import consulo.task.TaskRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
@Singleton
public class JiraServerManager {

    public static final Class<JiraServerListener> JIRA_SERVER_CHANGED = JiraServerListener.class;
    public static final Class<JiraServerNotConfiguredServer> JIRA_SERVER_REMOVED_ALL = JiraServerNotConfiguredServer.class;

    public static JiraServerManager getInstance() {
        return ApplicationManager.getApplication().getInstance(JiraServerManager.class);
    }

    public int getSelectedServerIndex(@Nonnull Project project) {
        return getSelectedProjectServer(project);
    }

    public boolean hasJiraServerConfigured(@Nonnull Project project) {
        return hasSelectedProjectServer(project);
    }

    @Nullable
    public JiraServer getCurrentJiraServer(@Nonnull Project project) {
        TaskRepository[] allRepositories = TaskManager.getManager(project).getAllRepositories();

        for (TaskRepository repository : allRepositories) {
            if (repository instanceof JiraRepository jiraRepository) {
                boolean token = jiraRepository.isUseBearerTokenAuthentication();

                JiraServer server = new JiraServer();
                server.setType(token ? AuthType.API_TOKEN : AuthType.USER_PASS);
                server.setUrl(jiraRepository.getUrl());
                server.setUsername(jiraRepository.getUsername());
                server.setPassword(jiraRepository.getPassword());
                return server;
            }
        }

        return null;
    }

    public void setServers(@Nonnull Project project, SimpleSelectableList<JiraServer> servers) {

        // Updates Global Servers
        List<JiraServer> globalServers = servers.getItems().stream().filter(JiraServer::isShared).collect(Collectors.toList());
        getJiraServerApplicationManager().setServers(globalServers);

        // Updates Project Servers
        List<JiraServer> projectServers = servers.getItems().stream().filter(server -> !server.isShared()).collect(Collectors.toList());
        getJiraServerProjectManager(project).setServers(projectServers);
        getJiraServerProjectManager(project).setSelectedServer(servers.getSelectedItemIndex());

        ApplicationManager.getApplication().getMessageBus().syncPublisher(JIRA_SERVER_CHANGED).onChangeSelectedServer();

        if (servers.isEmpty()) {
            ApplicationManager.getApplication().getMessageBus().syncPublisher(JIRA_SERVER_REMOVED_ALL).notConfiguredServer();
        }

    }

    @Nullable
    public JiraRestApi getJiraRestApi(@Nonnull Project project) {
        return convertFrom(getCurrentJiraServer(project));
    }

    @Nonnull
    public JiraRestApi getJiraRestApiFrom(@Nonnull JiraServer jiraServer) {
        return convertFrom(jiraServer);
    }

    @Nullable
    private JiraRestApi convertFrom(@Nullable JiraServer jiraServer) {
        if (isNull(jiraServer)) {
            return null;
        }

        return new JiraRestApi(new JiraRestTemplate(jiraServer));
    }

    @Deprecated
    public SimpleSelectableList<JiraServer> getAllServers(@Nonnull Project project) {
        SimpleSelectableList<JiraServer> allServers = SimpleSelectableList.of(getGlobalServers());

        JiraServerProjectManager serverProjectManager = getJiraServerProjectManager(project);
        allServers.addAll(serverProjectManager.getServers());

        if (serverProjectManager.hasSelectedServer()) {
            allServers.selectItem(serverProjectManager.getSelectedServerIndex());
        }

        return allServers;
    }

    public List<JiraServer> getGlobalServers() {
        return getJiraServerApplicationManager().getServers();
    }

    public boolean hasSelectedProjectServer(@Nonnull Project project) {
        return getAllServers(project).hasSelectedItem();
    }

    public int getSelectedProjectServer(@Nonnull Project project) {
        return getJiraServerProjectManager(project).getSelectedServerIndex();
    }

    private JiraServerApplicationManager getJiraServerApplicationManager() {
        return ApplicationManager.getApplication().getInstance(JiraServerApplicationManager.class);
    }

    private JiraServerProjectManager getJiraServerProjectManager(@Nonnull Project project) {
        return project.getInstance(JiraServerProjectManager.class);
    }
}
