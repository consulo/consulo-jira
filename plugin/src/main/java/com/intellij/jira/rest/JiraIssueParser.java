package com.intellij.jira.rest;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.intellij.jira.rest.model.JiraCreatedIssue;
import com.intellij.jira.rest.model.JiraGroup;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueAttachment;
import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.rest.model.metadata.JiraIssueCreateMetadata;
import com.intellij.jira.rest.model.JiraIssueLinkType;
import com.intellij.jira.rest.model.JiraIssuePriority;
import com.intellij.jira.rest.model.JiraIssueTransition;
import com.intellij.jira.rest.model.JiraIssueUser;
import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.jira.rest.model.JiraPermission;
import com.intellij.jira.util.JiraGsonUtil;
import com.intellij.tasks.jira.JiraRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Objects.isNull;

public class JiraIssueParser {
    private static final Type ISSUES_WRAPPER_TYPE = (new TypeToken<JiraIssuesWrapper<JiraIssue>>(){}).getType();
    private static final Type ISSUE_TRANSITION_WRAPPER_TYPE = (new TypeToken<JiraIssueTransitionsWrapper<JiraIssueTransition>>(){}).getType();

    private JiraIssueParser() { }


    public static JiraIssue parseIssue(String response){
        return JiraGsonUtil.getAs(response, JiraIssue.class);
    }

    public static JiraCreatedIssue parseCreatedIssue(String response) {
        return JiraGsonUtil.getAs(response, JiraCreatedIssue.class);
    }

    public static List<JiraIssue> parseIssues(String response){
        JiraIssuesWrapper<JiraIssue> wrapper = JiraRepository.GSON.fromJson(response, ISSUES_WRAPPER_TYPE);
        if (isNull(wrapper)) {
            return new ArrayList<>();
        }

        return wrapper.getIssues();
    }

    public static List<JiraIssueTransition> parseIssueTransitions(String response){
        JiraIssueTransitionsWrapper<JiraIssueTransition> wrapper = JiraRepository.GSON.fromJson(response, ISSUE_TRANSITION_WRAPPER_TYPE);
        if (isNull(wrapper)) {
            return new ArrayList<>();
        }

        return wrapper.getTransitions();
    }

    public static JiraIssueUser parseUser(String response) {
        return JiraGsonUtil.getAs(response, JiraIssueUser.class);
    }

    public static List<JiraIssueUser> parseUsers(String response) {
        return JiraGsonUtil.getAsList(response, JiraIssueUser[].class);
    }

    public static JiraIssueComment parseIssueComment(String response){
        return JiraGsonUtil.getAs(response, JiraIssueComment.class);
    }

    public static List<JiraIssuePriority> parseIssuePriorities(String response){
        return JiraGsonUtil.getAsList(response, JiraIssuePriority[].class);
    }

    public static List<JiraIssueLinkType> parseIssueLinkTypes(String response){
        JiraIssueLinkTypesWrapper wrapper = JiraGsonUtil.getAs(response, JiraIssueLinkTypesWrapper.class);
        if(isNull(wrapper)){
            return new ArrayList<>();
        }

        return wrapper.getIssueLinkTypes();
    }

    public static List<JiraGroup> parseGroups(String response){
        JiraGroupsWrapper wrapper = JiraGsonUtil.getAs(response, JiraGroupsWrapper.class);
        if(isNull(wrapper)){
            return new ArrayList<>();
        }

        return wrapper.getGroups();
    }

    public static List<String> parseRoles(String response){
        JsonObject jsonObject = JiraGsonUtil.getAs(response, JsonObject.class);
        if (isNull(jsonObject)) {
            return new ArrayList<>();
        }

        return new ArrayList<>(jsonObject.keySet());
    }

    public static LinkedHashMap<String, JiraPermission> parsePermissions(String response){
        JiraPermissionsWrapper jiraPermissionsWrapper = JiraGsonUtil.getAs(response, JiraPermissionsWrapper.class);
        if (isNull(jiraPermissionsWrapper)) {
            return new LinkedHashMap<>();
        }

        return jiraPermissionsWrapper.getPermissions();
    }

    public static JiraIssueWorklog parseIssueWorklog(String response){
        return JiraGsonUtil.getAs(response, JiraIssueWorklog.class);
    }

    public static List<JiraIssueAttachment> parseIssueAttachments(String response){
        return JiraGsonUtil.getAsList(response, JiraIssueAttachment[].class);
    }

    public static JiraIssueCreateMetadata parseIssueCreateMeta(String response) {
        return JiraGsonUtil.getAs(response, JiraIssueCreateMetadata.class);
    }

    public static JiraSuggestions parseLabels(String response) {
        return JiraGsonUtil.getAs(response, JiraSuggestions.class);
    }

}
