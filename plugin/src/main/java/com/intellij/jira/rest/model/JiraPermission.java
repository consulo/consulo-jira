package com.intellij.jira.rest.model;

public class JiraPermission {

    private String id;
    private String key;
    private String name;
    private String description;
    private String type;
    private boolean havePermission;

    public JiraPermission() { }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public boolean isHavePermission() {
        return havePermission;
    }

}
