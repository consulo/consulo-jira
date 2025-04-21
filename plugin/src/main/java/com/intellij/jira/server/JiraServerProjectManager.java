package com.intellij.jira.server;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.component.persist.PersistentStateComponent;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import consulo.component.persist.StoragePathMacros;
import consulo.credentialStorage.CredentialAttributes;
import consulo.credentialStorage.Credentials;
import consulo.credentialStorage.PasswordSafe;
import consulo.project.Project;
import consulo.util.xml.serializer.XmlSerializerUtil;
import consulo.util.xml.serializer.annotation.AbstractCollection;
import consulo.util.xml.serializer.annotation.Tag;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@State(name = "JiraServerManager", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
@ServiceAPI(ComponentScope.PROJECT)
@ServiceImpl
@Singleton
public class JiraServerProjectManager implements PersistentStateComponent<JiraServerProjectManager.State> {


    private List<JiraServer> myServers = new ArrayList<>();
    private int mySelectedServer = -1;

    private State myState = new State();

    public static JiraServerManager getInstance(@NotNull Project project) {
        return project.getInstance(JiraServerManager.class);
    }

    @Nullable
    @Override
    public State getState() {
        myState.selected = mySelectedServer;
        myState.servers = myServers;

        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        XmlSerializerUtil.copyBean(state, myState);

        myServers.clear();
        List<JiraServer> servers = state.servers;
        if (servers != null) {
            for (JiraServer server : servers) {
                CredentialAttributes credentialAttributes = new CredentialAttributes(server.getUrl());
                Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
                if (Objects.nonNull(credentials)) {
                    server.setUsername(credentials.getUserName());
                    server.setPassword(credentials.getPasswordAsString());
                }
            }

            myServers.addAll(servers);
        }

        mySelectedServer = state.selected;
    }

    public List<JiraServer> getServers() {
        return new ArrayList<>(myServers);
    }

    public int getSelectedServerIndex() {
        return mySelectedServer;
    }

    public boolean hasSelectedServer() {
        return mySelectedServer > -1;
    }

    public void setServers(List<JiraServer> servers) {
        List<JiraServer> oldJiraServers = new ArrayList<>(myServers);

        oldJiraServers.removeAll(servers);
        // Remove old stored credentials
        for (JiraServer server : oldJiraServers) {
            CredentialAttributes credentialAttributes = new CredentialAttributes(server.getUrl());
            PasswordSafe.getInstance().set(credentialAttributes, null);
        }

        // Store new credentials
        storeCredentials(servers);

        this.myServers = servers;
    }

    public void setSelectedServer(int selectedServer) {
        this.mySelectedServer = selectedServer;
    }

    private void storeCredentials(List<JiraServer> jiraServers) {
        for (JiraServer server : jiraServers) {
            CredentialAttributes credentialAttributes = new CredentialAttributes(server.getUrl());
            Credentials credentials = new Credentials(server.getUsername(), server.getPassword());

            PasswordSafe.getInstance().set(credentialAttributes, credentials);
        }
    }

    public static class State {
        @Tag("selected")
        public int selected;

        @AbstractCollection
        public List<JiraServer> servers = new ArrayList<>();
    }
}
