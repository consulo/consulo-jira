package com.intellij.jira.server;

import com.intellij.jira.helper.TransitionFieldHelper;
import com.intellij.jira.helper.TransitionFieldHelper.FieldEditorInfo;
import com.intellij.jira.rest.JiraRestClient;
import com.intellij.jira.rest.model.JiraCreatedIssue;
import com.intellij.jira.rest.model.JiraGroup;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.rest.model.JiraIssueLinkType;
import com.intellij.jira.rest.model.JiraIssuePriority;
import com.intellij.jira.rest.model.JiraIssueTransition;
import com.intellij.jira.rest.model.JiraIssueUser;
import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.jira.rest.model.JiraPermission;
import com.intellij.jira.rest.model.JiraPermissionType;
import com.intellij.jira.rest.model.metadata.JiraIssueCreateMetadata;
import com.intellij.jira.util.result.BodyResult;
import com.intellij.jira.util.result.EmptyResult;
import com.intellij.jira.util.result.Result;
import com.intellij.tasks.jira.JiraRepository;
import org.apache.commons.httpclient.NameValuePair;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class JiraRestApi {

    private static final Logger log = LoggerFactory.getLogger(JiraRestApi.class);

    private JiraRestClient jiraRestClient;

    public JiraRestApi(JiraRepository jiraRepository) {
        this.jiraRestClient = new JiraRestClient(jiraRepository);
    }


    public Result getIssue(String issueIdOrKey){
        try {
            JiraIssue issue = this.jiraRestClient.getIssue(issueIdOrKey);
            return BodyResult.ok(issue);
        } catch (Exception e) {
            log.error(String.format("Issue %s not found", issueIdOrKey));
        }

        return BodyResult.error();
    }

    public Result<JiraCreatedIssue> createIssue(Map<String, TransitionFieldHelper.FieldEditorInfo> createIssueFields) {
        try {
            JiraCreatedIssue issue = this.jiraRestClient.createIssue(createIssueFields);
            return BodyResult.ok(issue);
        } catch (Exception e) {
            log.error("Cannot create issue");
        }

        return BodyResult.error();
    }

    public List<JiraIssue> getIssues(String searchQuery) {
        try {
            return this.jiraRestClient.findIssues(searchQuery);
        } catch (Exception e) {
            log.error("No issues found");
            return new ArrayList<>();
        }
    }

    public List<JiraIssueTransition> getTransitions(String issueId){
        try {
            return jiraRestClient.getTransitions(issueId);
        } catch (Exception e) {
            log.error(String.format("No transitions was found for issue '%s'", issueId));
            return new ArrayList<>();
        }
    }


    public Result transitIssue(String issueId, String transitionId, Map<String, FieldEditorInfo> fields){
        try {
            String response = jiraRestClient.transitIssue(issueId, transitionId, fields);
            return EmptyResult.create(response);
        } catch (Exception e) {
            log.error(String.format("Error executing transition '%s' in issue '%s'", transitionId, issueId));
            return EmptyResult.error();
        }
    }

    public List<JiraIssueUser> getIssueAssignableUsers(String issueKey){
        try {
            return jiraRestClient.getIssueAssignableUsers(issueKey);
        } catch (Exception e) {
            log.error("Error fetching users to assign");
            return new ArrayList<>();
        }
    }

    public List<JiraIssueUser> getProjectAssignableUsers(String projectKey) {
        try {
            return jiraRestClient.getProjectAssignableUsers(projectKey);
        } catch (Exception e) {
            log.error("Error fetching users to assign");
            return new ArrayList<>();
        }
    }


    public Result assignUserToIssue(String accountId,  String username, String issueKey){
        try {
            String response = jiraRestClient.assignUserToIssue(accountId, username, issueKey);
            return EmptyResult.create(response);
        } catch (Exception e) {
            log.error(String.format("Error assigning user with accoundId = '%s' to issue '%s'", accountId, issueKey));
            return EmptyResult.error();
        }
    }

    @Nullable
    public JiraIssueComment getComment(String issueKey, String commentId) {
        JiraIssueComment comment = null;
        try {
            comment = jiraRestClient.getComment(issueKey, commentId);
        } catch (Exception e) {
            log.error(String.format("Comment with id = %s doesn't exists", commentId));
        }

        return comment;
    }

    public Result addIssueComment(String body, String issueKey, String viewableBy){
        try {
            JiraIssueComment comment = jiraRestClient.addCommentToIssue(body, issueKey, viewableBy);
            return BodyResult.ok(comment);
        } catch (Exception e) {
            log.error(String.format("Error creating comment in issue '%s'", issueKey));
            return BodyResult.error();
        }
    }

    public Result editIssueComment(String issueKey, String commentId, String body, String viewableBy){
        try {
            JiraIssueComment comment = jiraRestClient.editIssueComment(issueKey, commentId, body, viewableBy);
            return BodyResult.ok(comment);
        } catch (Exception e) {
            log.error(String.format("Error editing comment in issue '%s'", issueKey));
            return BodyResult.error();
        }
    }


    public Result deleteIssueComment(String issueKey, String commentId) {
        try {
            String response = jiraRestClient.deleteCommentToIssue(issueKey, commentId);
            return EmptyResult.create(response);
        } catch (Exception e) {
            log.error(String.format("Error deleting comment in issue '%s'", issueKey));
            return EmptyResult.error();
        }

    }

    public List<JiraIssuePriority> getIssuePriorities() {
        try {
            return jiraRestClient.getIssuePriorities();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Result changeIssuePriority(String priorityName, String issueIdOrKey) {
        try {
            String response = jiraRestClient.changeIssuePriority(priorityName, issueIdOrKey);
            return EmptyResult.create(response);
        } catch (Exception e) {
            e.printStackTrace();
            return EmptyResult.error();
        }

    }

    public boolean userHasPermission(JiraPermissionType... permissionTypes) {
        LinkedHashMap<String, JiraPermission> permissions = new LinkedHashMap<>();
        try {
            String permissionList = Arrays.stream(permissionTypes)
                    .map(JiraPermissionType::toString)
                    .collect(Collectors.joining(","));

            permissions = jiraRestClient.findUserPermissions(new NameValuePair("permissions", permissionList));
        } catch (Exception e) {
            log.error("Current user has not permission to do this action");
        }

        return isHavePermission(permissions, permissionTypes);
    }

    public boolean userHasPermissionOnIssue(String issueKey, JiraPermissionType... permissionTypes){
        LinkedHashMap<String, JiraPermission> permissions = new LinkedHashMap<>();
        try {
            permissions = jiraRestClient.findUserPermissionsOnIssue(issueKey, permissionTypes);
        } catch (Exception e) {
            log.error("Current user has not permission to do this action");
        }

        return isHavePermission(permissions, permissionTypes);
    }

    private boolean isHavePermission(LinkedHashMap<String, JiraPermission> permissions, JiraPermissionType... permissionTypes) {
        for (JiraPermissionType permission : permissionTypes) {
            JiraPermission jiraPermission = permissions.get(permission.toString());
            if(Objects.isNull(jiraPermission)){
                jiraPermission = permissions.get(permission.getOldPermission());
            }

            if (Objects.isNull(jiraPermission) || !jiraPermission.isHavePermission()) {
                return false;
            }

        }

        return true;
    }


    public List<JiraIssueLinkType> getIssueLinkTypes(){
        try {
            return jiraRestClient.getIssueLinkTypes();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<JiraGroup> getGroups(){
        try {
            return jiraRestClient.getGroups();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public JiraIssueWorklog getWorklog(String issueKey, String worklogId){
        JiraIssueWorklog worklog = null;
        try {
            worklog = jiraRestClient.getWorklog(issueKey, worklogId);
        } catch (Exception e) {
            log.error(String.format("WorkLog with id = %s doesn't exists", worklogId));
        }

        return worklog;
    }

    public void testConnection() throws Exception {
        findCurrentUser();
    }

    public List<String> getProjectRoles(String projectKey) {
        try {
            return jiraRestClient.getProjectRoles(projectKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Result addIssueLink(String linkType, String inIssueKey, String outIssueKey) {
        try {
            Integer statusCode = jiraRestClient.addIssueLink(linkType, inIssueKey, outIssueKey);
            return statusCode == 201 ? BodyResult.ok(statusCode) :  BodyResult.error();
        } catch (Exception e) {
            log.error("Error creating issue link");
            return BodyResult.error();
        }
    }

    public Result deleteIssueLink(String issueLinkId) {
        try {
            Integer statusCode = jiraRestClient.deleteIssueLink(issueLinkId);
            return statusCode == 204 ? BodyResult.ok(statusCode) :  BodyResult.error();
        } catch (Exception e) {
            log.error("Error deleting issue link");
            return BodyResult.error();
        }
    }

    public String getUsername(){
        return jiraRestClient.getUsername();
    }

    public Result addIssueWorklog(String issueKey, List<FieldEditorInfo> worklogFields, String remainingEstimate) {
        try {
            JiraIssueWorklog worklog = jiraRestClient.addIssueWorklog(issueKey, worklogFields, remainingEstimate);
            return BodyResult.ok(worklog);
        } catch (Exception e) {
            log.error(String.format("Error creating worklog in issue '%s'", issueKey));
            return BodyResult.error();
        }
    }

    public Result editIssueWorklog(String issueKey, String workLogId, List<FieldEditorInfo> worklogFields, String remainingEstimate) {
        try {
            JiraIssueWorklog worklog = jiraRestClient.updateIssueWorklog(issueKey, workLogId, worklogFields, remainingEstimate);
            return BodyResult.ok(worklog);
        } catch (Exception e) {
            log.error(String.format("Error editing worklog in issue '%s'", issueKey));
            return BodyResult.error();
        }
    }

    public Result deleteIssueWorklog(String issueKey, String worklogId, String remainingEstimate) {
        try {
            Integer statusCode = jiraRestClient.deleteIssueWorklog(issueKey, worklogId, remainingEstimate);
            return statusCode == 204 ? BodyResult.ok(statusCode) :  BodyResult.error();
        } catch (Exception e) {
            log.error("Error deleting issue link");
            return BodyResult.error();
        }
    }

    public Result watchIssue(String issueKey) {
        try {
            Integer statusCode = jiraRestClient.watchIssue(issueKey);
            return statusCode == 204 ? BodyResult.ok(statusCode) :  BodyResult.error();
        } catch (Exception e) {
            log.error("Error watching issue");
            return BodyResult.error();
        }
    }

    public Result unwatchIssue(String issueKey, String accountId, String username) {
        try {
            Integer statusCode = jiraRestClient.unwatchIssue(issueKey, accountId, username);
            return statusCode == 204 ? BodyResult.ok(statusCode) :  BodyResult.error();
        } catch (Exception e) {
            log.error("Error watching issue");
            return BodyResult.error();
        }
    }

    public Result getCurrentUser() {
        try {
            return BodyResult.ok(findCurrentUser());
        } catch (Exception e) {
            log.error("Error getting current user");
            return BodyResult.error();
        }
    }

    public Result addIssueAttachment(String issueKey, File attachment) {
        try {
            return BodyResult.ok(jiraRestClient.addIssueAttachment(issueKey, attachment));
        } catch (Exception e) {
            log.error("Error attaching on issue " + issueKey);
            return BodyResult.error();
        }
    }

    public Result deleteIssueAttachment(String attachmentId) {
        try {
            Integer statusCode = jiraRestClient.deleteIssueAttachment(attachmentId);
            return statusCode == 204 ? BodyResult.ok(statusCode) : BodyResult.error();
        } catch (Exception e) {
            log.error("Error deleting attachment");
            return BodyResult.error();
        }
    }

    public JiraIssueCreateMetadata getIssueCreateMeta() {
        try {
            return jiraRestClient.getIssueCreateMeta();
        } catch (Exception e) {
            // TODO: refactor!!
            return null;
        }

    }

    public List<String> findLabels(String prefix, String autoCompleteUrl) {
        try {
            return jiraRestClient.findLabels(prefix, autoCompleteUrl);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private JiraIssueUser findCurrentUser() throws Exception {
        return jiraRestClient.getCurrentUser();
    }
}
