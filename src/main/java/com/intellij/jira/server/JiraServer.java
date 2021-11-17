package com.intellij.jira.server;

import com.intellij.jira.server.auth.AuthType;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.intellij.openapi.util.text.StringUtil.trim;

@Tag("JiraServer")
public class JiraServer {

    private static final AuthType DEFAULT_AUTH_TYPE = AuthType.USER_PASS;

    private String url;

    private String username;
    private String password;

    private AuthType type;
    private boolean shared;

    public JiraServer() { }

    private JiraServer(String url, String username, String password, AuthType type, boolean shared) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.type = type;
        this.shared = shared;
    }

    private JiraServer(JiraServer other){
        this(other.getUrl(), other.getUsername(), other.getPassword(), other.getType(), other.isShared());
    }

    public void withUserAndPass(String url, String username, String password, boolean shared) {
        setUrl(url);
        setUsername(username);
        setPassword(password);
        setType(AuthType.USER_PASS);
        setShared(shared);
    }

    public void withApiToken(String url, String useremail, String apiToken, boolean shared) {
        setUrl(url);
        setUsername(useremail);
        setPassword(apiToken);
        setType(AuthType.API_TOKEN);
        setShared(shared);
    }

    @Attribute("url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Transient
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Transient
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Attribute("type")
    public AuthType getType() {
        return type == null ? DEFAULT_AUTH_TYPE : type;
    }

    public void setType(AuthType type) {
        this.type = type == null ? DEFAULT_AUTH_TYPE : type;
    }

    @Attribute("shared")
    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    @Transient
    public boolean hasUserAndPassAuth() {
        return AuthType.USER_PASS == getType();
    }

    @Transient
    public String getPresentableName(){
        return StringUtil.isEmpty(trim(getUrl())) ? "<undefined>" : getUrl();
    }

    @NotNull
    @Override
    public JiraServer clone(){
        return new JiraServer(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JiraServer that = (JiraServer) o;
        return Objects.equals(url, that.url) && Objects.equals(username, that.username) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, username, password);
    }


    @Override
    public String toString() {
        return getPresentableName();
    }
}
