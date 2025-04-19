package com.intellij.jira.server;

import com.intellij.jira.rest.client.JiraRestTemplate;
import com.intellij.jira.util.SimpleSelectableList;
import consulo.application.ApplicationManager;
import consulo.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class JiraServerManager  {

    @Topic.AppLevel
    public static final Topic<JiraServerListener> JIRA_SERVER_CHANGED = Topic.create("Jira Server Changes", JiraServerListener.class);
    @Topic.AppLevel
    public static final Topic<JiraServerNotConfiguredServer> JIRA_SERVER_REMOVED_ALL = Topic.create("Jira Server Changes", JiraServerNotConfiguredServer.class);

    public static JiraServerManager getInstance() {
        return ApplicationManager.getApplication().getService(JiraServerManager.class);
    }

    public int getSelectedServerIndex(@NotNull Project project){
        return getSelectedProjectServer(project);
    }

    public boolean hasJiraServerConfigured(@NotNull Project project){
        return hasSelectedProjectServer(project);
    }

    @Nullable
    public JiraServer getCurrentJiraServer(@NotNull Project project){
        SimpleSelectableList<JiraServer> allServers = getAllServers(project);
        if (allServers.hasSelectedItem()) {
            return allServers.getSelectedItem();
        }

        return null;
    }

    public void setServers(@NotNull Project project, SimpleSelectableList<JiraServer> servers) {

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
    public JiraRestApi getJiraRestApi(@NotNull Project project){
        return convertFrom(getCurrentJiraServer(project));
    }

    @NotNull
    public JiraRestApi getJiraRestApiFrom(@NotNull JiraServer jiraServer){
        return convertFrom(jiraServer);
    }

    @Nullable
    private JiraRestApi convertFrom(@Nullable JiraServer jiraServer){
        if(isNull(jiraServer)){
            return null;
        }

        return new JiraRestApi(new JiraRestTemplate(jiraServer));
    }

    public SimpleSelectableList<JiraServer> getAllServers(@NotNull Project project) {
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

    public boolean hasSelectedProjectServer(@NotNull Project project) {
        return getAllServers(project).hasSelectedItem();
    }

    public int getSelectedProjectServer(@NotNull Project project) {
        return getJiraServerProjectManager(project).getSelectedServerIndex();
    }

    private JiraServerApplicationManager getJiraServerApplicationManager() {
        return ApplicationManager.getApplication().getService(JiraServerApplicationManager.class);
    }

    private JiraServerProjectManager getJiraServerProjectManager(@NotNull Project project) {
        return project.getService(JiraServerProjectManager.class);
    }

    public interface JiraServerListener {

        void onChangeSelectedServer();

    }

    public interface JiraServerNotConfiguredServer {

        void notConfiguredServer();
    }

}
