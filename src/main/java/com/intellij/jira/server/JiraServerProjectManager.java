package com.intellij.jira.server;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@State(name = "JiraServerManager", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class JiraServerProjectManager implements PersistentStateComponent<JiraServerProjectManager.State>  {


    private List<JiraServer> myServers = new ArrayList<>();
    private int mySelectedServer = -1;

    private State myState = new State();

    public static JiraServerManager getInstance(@NotNull Project project) {
        return project.getService(JiraServerManager.class);
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

    public int getSelectedServerIndex(){
        return mySelectedServer;
    }

    public boolean hasSelectedServer(){
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

    public static class State{
        @Tag("selected")
        public int selected;

        @XCollection(propertyElementName = "servers")
        public List<JiraServer> servers;

    }
}
