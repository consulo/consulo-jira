package com.intellij.jira.rest.model;

import java.util.Objects;

public class JiraProjectVersion {

    private String id;
    private String name;
    private String self;
    private boolean archived;
    private boolean released;

    public JiraProjectVersion() { }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSelf() {
        return self;
    }

    public boolean isArchived() {
        return archived;
    }

    public boolean isReleased() {
        return released;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JiraProjectVersion that = (JiraProjectVersion) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
