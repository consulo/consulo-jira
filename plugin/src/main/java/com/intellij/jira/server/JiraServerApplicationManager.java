package com.intellij.jira.server;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.component.persist.PersistentStateComponent;
import consulo.component.persist.RoamingType;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import consulo.credentialStorage.CredentialAttributes;
import consulo.credentialStorage.Credentials;
import consulo.credentialStorage.PasswordSafe;
import consulo.util.xml.serializer.XmlSerializerUtil;
import consulo.util.xml.serializer.annotation.AbstractCollection;
import jakarta.inject.Singleton;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@State(name = "JiraServerApplicationManager", storages = @Storage(value = "jira.xml", roamingType = RoamingType.DISABLED))
@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
@Singleton
public class JiraServerApplicationManager implements PersistentStateComponent<JiraServerApplicationManager.State> {

    private List<JiraServer> myServers = new ArrayList<>();
    private State myState = new State();

    @Override
    public @Nullable State getState() {
        myState.servers = myServers;

        return myState;
    }

    @Override
    public void loadState(@Nonnull State state) {
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
    }

    public List<JiraServer> getServers() {
        return new ArrayList<>(myServers);
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

    private void storeCredentials(List<JiraServer> jiraServers) {
        for (JiraServer server : jiraServers) {
            CredentialAttributes credentialAttributes = new CredentialAttributes(server.getUrl());
            Credentials credentials = new Credentials(server.getUsername(), server.getPassword());

            PasswordSafe.getInstance().set(credentialAttributes, credentials);
        }
    }

    public static class State {

        @AbstractCollection
        public List<JiraServer> servers = new ArrayList<>();

    }
}
