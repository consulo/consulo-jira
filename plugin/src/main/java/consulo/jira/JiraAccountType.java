/*
 * Copyright 2013-2026 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package consulo.jira;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import consulo.accountManagement.AccountAuthorizeConfig;
import consulo.accountManagement.AccountConnection;
import consulo.accountManagement.AccountDetails;
import consulo.accountManagement.AccountScope;
import consulo.accountManagement.AccountType;
import consulo.accountManagement.NoScope;
import consulo.accountManagement.authorize.BasicAuthorizeConfig;
import consulo.accountManagement.authorize.TokenAuthorizeConfig;
import consulo.annotation.component.ExtensionImpl;
import consulo.jira.icon.JiraIconGroup;
import consulo.jira.localize.JiraLocalize;
import consulo.localize.LocalizeValue;
import consulo.ui.image.Image;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Jira Cloud signs in with the e-mail and an API token over basic authentication, Jira Server and Data Center with a
 * password or a personal access token.
 *
 * @author VISTALL
 * @since 2026-11-09
 */
@ExtensionImpl
public class JiraAccountType implements AccountType<NoScope> {
    public static final String ID = "jira";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public LocalizeValue getName() {
        return JiraLocalize.jiraName();
    }

    @Override
    public Image getIcon() {
        return JiraIconGroup.jiraicon();
    }

    @Override
    public Class<NoScope> getScopeClass() {
        return NoScope.class;
    }

    @Override
    public boolean isServerEditable() {
        return true;
    }

    @Override
    public List<? extends AccountAuthorizeConfig> getAuthorizeConfigs() {
        return List.of(
            new BasicAuthorizeConfig() {
                @Override
                public boolean isDefault() {
                    return true;
                }
            },
            new TokenAuthorizeConfig() {
                @Override
                public @Nullable String buildNewTokenUrl(@Nullable String serverUrl, Set<? extends AccountScope> scopes) {
                    if (serverUrl == null) {
                        return null;
                    }
                    return JiraClient.trimServerUrl(serverUrl)
                        + "/secure/ViewProfile.jspa?selectedTab=com.atlassian.pats.pats-plugin:jira-user-personal-access-tokens";
                }
            }
        );
    }

    @Override
    public CompletableFuture<AccountDetails> loadDetails(AccountConnection connection) {
        return JiraClient.create(connection).get("myself").thenApply(element -> {
            JsonObject user = element.getAsJsonObject();
            String name = JiraJson.string(user, "displayName");
            String login = JiraJson.string(user, "emailAddress");
            if (login == null) {
                login = JiraJson.string(user, "name");
            }

            String avatarUrl = null;
            JsonElement avatars = user.get("avatarUrls");
            if (avatars != null && avatars.isJsonObject()) {
                avatarUrl = JiraJson.string(avatars.getAsJsonObject(), "48x48");
            }
            return new AccountDetails(name == null ? (login == null ? "" : login) : name, login, avatarUrl);
        });
    }
}
