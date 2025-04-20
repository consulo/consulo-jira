package com.intellij.jira.rest;

import com.intellij.jira.rest.model.JiraPermission;

import java.util.LinkedHashMap;

public class JiraPermissionsWrapper {
    private LinkedHashMap<String, JiraPermission> permissions = new LinkedHashMap<>();

    public JiraPermissionsWrapper() { }

    public LinkedHashMap<String, JiraPermission> getPermissions() {
        return permissions;
    }

}
