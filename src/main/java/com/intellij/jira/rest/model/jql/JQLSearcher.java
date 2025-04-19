package com.intellij.jira.rest.model.jql;

import consulo.util.lang.StringUtil;
import consulo.util.xml.serializer.annotation.Attribute;
import consulo.util.xml.serializer.annotation.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

import static consulo.util.lang.StringUtil.trim;

@Tag("JQLSearcher")
public class JQLSearcher {

    private String myId;

    private String alias;
    private String jql;
    @Attribute("shared")
    private boolean shared;

    public JQLSearcher() {
        this("", "", false);
    }

    public JQLSearcher(@Nullable String alias, String jql, boolean shared) {
        this(UUID.randomUUID().toString(), alias, jql, shared);
    }

    private JQLSearcher(JQLSearcher other){
        this(other.getId(), other.getAlias(), other.getJql(), other.isShared());
    }

    private JQLSearcher(@NotNull String id, @Nullable String alias, String jql, boolean shared) {
        myId = id;
        setAlias(alias);
        setJql(jql);
        setShared(shared);
    }

    @Attribute("alias")
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = StringUtil.isEmpty(alias) ? "Undefined" : trim(alias);
    }

    @Attribute("jql")
    public String getJql() {
        return jql;
    }

    public void setJql(String jql) {
        this.jql = trim(jql);
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public String getId() {
        return myId;
    }

    @Override
    public String toString() {
        return alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JQLSearcher that = (JQLSearcher) o;
        return Objects.equals(alias, that.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias);
    }

    @Override
    public JQLSearcher clone(){
        return new JQLSearcher(this);
    }

}
