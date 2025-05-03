package com.intellij.jira.rest.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.intellij.jira.helper.TransitionFieldHelper.FieldEditorInfo;
import com.intellij.jira.rest.model.*;
import com.intellij.jira.rest.model.metadata.JiraIssueCreateMetadata;
import com.intellij.jira.util.JiraGsonUtil;
import consulo.util.lang.StringUtil;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import jakarta.annotation.Nonnull;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static com.intellij.jira.rest.JiraIssueParser.*;
import static com.intellij.jira.ui.dialog.AddCommentDialog.ALL_USERS;
import static com.intellij.jira.util.JiraGsonUtil.*;
import static com.intellij.jira.util.JiraIssueField.KEY;
import static java.util.Objects.nonNull;

public class JiraRestClient {

    private static final Integer MAX_ISSUES_RESULTS = 500;
    private static final Integer MAX_USERS_RESULTS = 200;

    private static final String ISSUE = "issue";
    private static final String TRANSITIONS = "transitions";
    private static final String SEARCH = "search";
    private static final String COMMENT = "comment";
    private static final String WORKLOG = "worklog";

    private final JiraRestTemplate myJiraRestTemplate;

    public JiraRestClient(@Nonnull JiraRestTemplate jiraRestTemplate) {
        myJiraRestTemplate = jiraRestTemplate;
    }

    public JiraIssue getIssue(String issueIdOrKey) throws Exception {
        GetMethod method = new GetMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueIdOrKey));
        method.setQueryString(new NameValuePair[]{new NameValuePair("expand", "renderedFields")});
        String response = myJiraRestTemplate.executeMethod(method);
        return parseIssue(response);
    }

    public JiraCreatedIssue createIssue(Map<String, FieldEditorInfo> createIssueFields) throws Exception {
        PostMethod method = new PostMethod(myJiraRestTemplate.getRestUrl(ISSUE));
        String requestBody = getCreateIssueRequestBody(createIssueFields);
        method.setRequestEntity(createJsonEntity(requestBody));
        String response = myJiraRestTemplate.executeMethod(method);

        return parseCreatedIssue(response);
    }

    public List<JiraIssue> findIssues(String searchQuery) throws Exception {
        GetMethod method = getBasicSearchMethod(searchQuery, MAX_ISSUES_RESULTS);
        method.setQueryString(method.getQueryString() + "&fields=*all&expand=renderedFields");
        String response = myJiraRestTemplate.executeMethod(method);
        return parseIssues(response);
    }

    public List<JiraIssueTransition> getTransitions(String issueId) throws Exception {
        GetMethod method = new GetMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueId, TRANSITIONS));
        method.setQueryString(new NameValuePair[]{new NameValuePair("expand", "transitions.fields")});
        String response = myJiraRestTemplate.executeMethod(method);
        return parseIssueTransitions(response);
    }

    public String transitIssue(String issueId, String transitionId, Map<String, FieldEditorInfo> fields) throws Exception {
        String requestBody = getTransitionRequestBody(transitionId, fields);
        PostMethod method = new PostMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueId, TRANSITIONS));
        method.setRequestEntity(createJsonEntity(requestBody));
        return myJiraRestTemplate.executeMethod(method);
    }

    public List<JiraIssueUser> getIssueAssignableUsers(String issueKey) throws Exception {
        return fetchUsers(new NameValuePair("issueKey", issueKey));
    }

    public List<JiraIssueUser> getProjectAssignableUsers(String projectKey) throws Exception {
        return fetchUsers(new NameValuePair("project", projectKey));
    }

    private List<JiraIssueUser> fetchUsers(NameValuePair issueOrProjectParam) throws Exception {
        GetMethod method = new GetMethod(myJiraRestTemplate.getRestUrl("user", "assignable", SEARCH));

        List<JiraIssueUser> newUsers;
        List<JiraIssueUser> jiraUsers = new ArrayList<>();

        do {
            method.setQueryString(new NameValuePair[]{
                    issueOrProjectParam,
                    new NameValuePair("startAt", String.valueOf(jiraUsers.size())),
                    new NameValuePair("maxResults", String.valueOf(MAX_USERS_RESULTS)),
            });

            String response = myJiraRestTemplate.executeMethod(method);
            newUsers = parseUsers(response);
            jiraUsers.addAll(newUsers);
        } while (newUsers.size() == MAX_USERS_RESULTS);

        return jiraUsers;
    }

    public String assignUserToIssue(String accountId,  String username, String issueKey) throws Exception {
        String requestBody = accountId != null ? JiraGsonUtil.createObject("accountId", accountId).toString() : JiraGsonUtil.createObject("name", username).toString();
        PutMethod method = new PutMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueKey, "assignee"));
        method.setRequestEntity(createJsonEntity(requestBody));
        return myJiraRestTemplate.executeMethod(method);
    }

    public JiraIssueComment getComment(String issueKey, String commentId) throws Exception {
        GetMethod method = new GetMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueKey, COMMENT, commentId));
        String response = myJiraRestTemplate.executeMethod(method);
        return parseIssueComment(response);
    }

    public JiraIssueComment addCommentToIssue(String body, String issueKey, String viewableBy) throws Exception {
        String requestBody = prepareCommentBody(body, viewableBy);
        PostMethod method = new PostMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueKey, COMMENT));
        method.setRequestEntity(createJsonEntity(requestBody));
        String response = myJiraRestTemplate.executeMethod(method);
        return parseIssueComment(response);
    }

    public JiraIssueComment editIssueComment(String issueKey, String commentId, String body, String viewableBy) throws Exception {
        String requestBody = prepareCommentBody(body, viewableBy);
        PutMethod method = new PutMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueKey, COMMENT, commentId));
        method.setRequestEntity(createJsonEntity(requestBody));
        String response = myJiraRestTemplate.executeMethod(method);
        return parseIssueComment(response);
    }


    private GetMethod getBasicSearchMethod(String jql, int maxResults) {
        GetMethod method = new GetMethod(myJiraRestTemplate.getRestUrl(SEARCH));
        method.setQueryString(new NameValuePair[]{new NameValuePair("jql", jql), new NameValuePair("maxResults", String.valueOf(maxResults))});
        return method;
    }

    private static RequestEntity createJsonEntity(String requestBody) {
        try {
            return new StringRequestEntity(requestBody, "application/json", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 encoding is not supported");
        }
    }

    public String deleteCommentToIssue(String issueKey, String commentId) throws Exception {
        DeleteMethod method = new DeleteMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueKey, "comment", commentId));
        return myJiraRestTemplate.executeMethod(method);
    }

    public List<JiraIssuePriority> getIssuePriorities() throws Exception {
        GetMethod method = new GetMethod(myJiraRestTemplate.getRestUrl("priority"));
        String response = myJiraRestTemplate.executeMethod(method);
        return parseIssuePriorities(response);
    }

    public String changeIssuePriority(String priorityName, String issueIdOrKey) throws Exception {
        String requestBody = "{\"update\": {\"priority\": [{\"set\": {\"name\": \"" + priorityName + "\"}}]}}";
        PutMethod method = new PutMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueIdOrKey));
        method.setRequestEntity(createJsonEntity(requestBody));
        return myJiraRestTemplate.executeMethod(method);
    }

    public LinkedHashMap<String, JiraPermission> findUserPermissionsOnIssue(String issueKey, JiraPermissionType... permissions) throws Exception {
        String permissionTypes = Arrays.stream(permissions)
                .map(JiraPermissionType::toString)
                .collect(Collectors.joining(","));

        NameValuePair[] queryParams = {
                new NameValuePair("issueKey", issueKey),
                new NameValuePair("permissions", permissionTypes)
        };

        return findUserPermissions(queryParams);
    }

    public LinkedHashMap<String, JiraPermission> findUserPermissions(NameValuePair... queryParams) throws Exception {
        GetMethod method = new GetMethod(myJiraRestTemplate.getRestUrl("mypermissions"));
        method.setQueryString(queryParams);

        String response = myJiraRestTemplate.executeMethod(method);
        return parsePermissions(response);
    }

    public List<JiraIssueLinkType> getIssueLinkTypes() throws Exception {
        GetMethod method = new GetMethod(myJiraRestTemplate.getRestUrl("issueLinkType"));
        String response = myJiraRestTemplate.executeMethod(method);
        return parseIssueLinkTypes(response);
    }

    public List<JiraGroup> getGroups() throws Exception {
        GetMethod method = new GetMethod(myJiraRestTemplate.getRestUrl("groups", "picker"));
        String response = myJiraRestTemplate.executeMethod(method);
        return parseGroups(response);
    }


    public String getDefaultSearchQuery() {
        return myJiraRestTemplate.getSearchQuery();
    }

    public List<String> getProjectRoles(String projectKey) throws Exception {
        GetMethod method = new GetMethod(myJiraRestTemplate.getRestUrl("project", projectKey, "role"));
        String response = myJiraRestTemplate.executeMethod(method);

        return parseRoles(response);
    }

    public Integer addIssueLink(String linkType, String inIssueKey, String outIssueKey) throws Exception {
        String requestBody = prepareIssueLinkBody(linkType, inIssueKey, outIssueKey);
        PostMethod method = new PostMethod(myJiraRestTemplate.getRestUrl("issueLink"));
        method.setRequestEntity(createJsonEntity(requestBody));
        myJiraRestTemplate.executeMethod(method);
        return method.getStatusCode();
    }

    public Integer deleteIssueLink(String issueLinkId) throws Exception {
        DeleteMethod method = new DeleteMethod(myJiraRestTemplate.getRestUrl("issueLink", issueLinkId));
        myJiraRestTemplate.executeMethod(method);
        return method.getStatusCode();
    }

    public String getUsername(){
        return myJiraRestTemplate.getUsername();
    }

    public JiraIssueWorklog getWorklog(String issueKey, String worklogId) throws Exception {
        GetMethod method = new GetMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueKey, WORKLOG, worklogId));
        String response = myJiraRestTemplate.executeMethod(method);

        return parseIssueWorklog(response);
    }

    public JiraIssueWorklog addIssueWorklog(String issueKey, List<FieldEditorInfo> worklogFields, String remainingEstimate) throws Exception {
        String requestBody = prepareWorklogBody(worklogFields);
        PostMethod method = new PostMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueKey, WORKLOG));
        if(StringUtil.isNotEmpty(remainingEstimate)){
            method.setQueryString("adjustEstimate=" + remainingEstimate);
        }
        method.setRequestEntity(createJsonEntity(requestBody));
        String response = myJiraRestTemplate.executeMethod(method);

        return parseIssueWorklog(response);
    }

    public JiraIssueWorklog updateIssueWorklog(String issueKey, String workLogId, List<FieldEditorInfo> worklogFields, String remainingEstimate) throws Exception {
        String requestBody = prepareWorklogBody(worklogFields);
        PutMethod method = new PutMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueKey, WORKLOG, workLogId));
        if(StringUtil.isNotEmpty(remainingEstimate)){
            method.setQueryString("adjustEstimate=" + remainingEstimate);
        }
        method.setRequestEntity(createJsonEntity(requestBody));
        String response = myJiraRestTemplate.executeMethod(method);

        return parseIssueWorklog(response);
    }

    public Integer deleteIssueWorklog(String issueKey, String worklogId, String remainingEstimate) throws Exception {
        DeleteMethod method = new DeleteMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueKey, WORKLOG, worklogId));
        if(StringUtil.isNotEmpty(remainingEstimate)){
            method.setQueryString("adjustEstimate=" + remainingEstimate);
        }

        myJiraRestTemplate.executeMethod(method);
        return method.getStatusCode();
    }

    public Integer watchIssue(String issueKey) throws Exception {
        PostMethod method = new PostMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueKey, "watchers"));

        myJiraRestTemplate.executeMethod(method);
        return method.getStatusCode();
    }

    public Integer unwatchIssue(String issueKey, String accountId, String username) throws Exception {
        DeleteMethod method = new DeleteMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueKey, "watchers"));
        if (accountId != null) {
            method.setQueryString(new NameValuePair[]{new NameValuePair("accountId", accountId)});
        } else {
            // For compatibility
            method.setQueryString(new NameValuePair[]{new NameValuePair("username", username)});
        }

        myJiraRestTemplate.executeMethod(method);
        return method.getStatusCode();
    }

    public JiraIssueUser getCurrentUser() throws Exception {
        GetMethod method = new GetMethod(myJiraRestTemplate.getRestUrl("myself"));
        String response = myJiraRestTemplate.executeMethod(method);

        return parseUser(response);
    }

    public List<JiraIssueAttachment> addIssueAttachment(String issueKey, File attachment) throws Exception {
        PostMethod method = new PostMethod(myJiraRestTemplate.getRestUrl(ISSUE, issueKey, "attachments"));
        method.addRequestHeader("X-Atlassian-Token", "no-check");
        Part[] parts = {new FilePart("file", attachment)};

        method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
        String response = myJiraRestTemplate.executeMethod(method);

        return parseIssueAttachments(response);
    }

    public Integer deleteIssueAttachment(String attachmentId) throws Exception {
        DeleteMethod method = new DeleteMethod(myJiraRestTemplate.getRestUrl("attachment", attachmentId));

        myJiraRestTemplate.executeMethod(method);
        return method.getStatusCode();
    }

    public JiraIssueCreateMetadata getIssueCreateMeta() throws Exception {
        GetMethod method = new GetMethod(myJiraRestTemplate.getRestUrl(ISSUE, "createmeta"));
        method.setQueryString(new NameValuePair[]{new NameValuePair("expand", "projects.issuetypes.fields")});

        String response = myJiraRestTemplate.executeMethod(method);

        return parseIssueCreateMeta(response);
    }

    public List<String> findLabels(String prefix, String autoCompleteUrl) throws Exception {
        GetMethod method = new GetMethod(autoCompleteUrl + prefix);
        String response = myJiraRestTemplate.executeMethod(method);

        return parseLabels(response).getSuggestionLabels();
    }

    private String getTransitionRequestBody(String transitionId, Map<String, FieldEditorInfo> fields) {
        JsonObject transition = new JsonObject();
        transition.add("transition", createIdObject(transitionId));

        // Update
        JsonObject updateObject = new JsonObject();

        // Comment
        FieldEditorInfo commentField = fields.remove("comment");
        if(nonNull(commentField) && !(commentField.getJsonValue() instanceof JsonNull)){
            updateObject.add("comment", commentField.getJsonValue());
        }

        // Work Log
        FieldEditorInfo worklogField = fields.remove("worklog");
        if(nonNull(worklogField)) {
            JsonElement worklogFieldValue = worklogField.getJsonValue();
            if (!(worklogFieldValue instanceof JsonNull)) {
                updateObject.add("worklog", worklogFieldValue);
            }
        }

        // Linked Issues
        FieldEditorInfo issueLinkField = fields.remove("issuelinks");
        if(nonNull(issueLinkField) && !(issueLinkField.getJsonValue() instanceof JsonNull)){
            updateObject.add("issuelinks", issueLinkField.getJsonValue());
        }

        if(updateObject.size() > 0){
            transition.add("update", updateObject);
        }

        //Fields
        JsonObject fieldsObject = new JsonObject();
        fields.forEach((key, value) -> {
            JsonElement jsonValue = value.getJsonValue();
            if(!(jsonValue instanceof JsonNull)){
                fieldsObject.add(key, jsonValue);
            }
        });

        if(fieldsObject.size() > 0){
            transition.add("fields", fieldsObject);
        }


        return transition.toString();
    }

    private String prepareWorklogBody(List<FieldEditorInfo> worklogFields){
        JsonObject worklogObject = new JsonObject();
        for(FieldEditorInfo editorInfo : worklogFields){
            JsonElement jsonValue = editorInfo.getJsonValue();
            if(!jsonValue.isJsonNull()){
                worklogObject.add(editorInfo.getName(), jsonValue);
            }
        }

        return worklogObject.toString();
    }

    private String prepareCommentBody(String body, String viewableBy){
        JsonObject commentBody = new JsonObject();
        commentBody.addProperty("body", body);

        if(!ALL_USERS.equals(viewableBy)){
            JsonObject visibility = new JsonObject();
            visibility.addProperty("type", "role");
            visibility.addProperty("value", viewableBy);
            commentBody.add("visibility", visibility);
        }

        return commentBody.toString();
    }

    private String prepareIssueLinkBody(String linkType, String inIssueKey, String outIssueKey) {
        JsonObject linkObject = new JsonObject();
        linkObject.add("type", createNameObject(linkType));
        linkObject.add("inwardIssue", createObject(KEY, inIssueKey));
        linkObject.add("outwardIssue", createObject(KEY, outIssueKey));

        return linkObject.toString();
    }

    private String getCreateIssueRequestBody(Map<String, FieldEditorInfo> createIssueFields) {
        JsonObject createIssueObject = new JsonObject();

        // Update
        JsonObject updateObject = new JsonObject();

        // Comment
        FieldEditorInfo commentField = createIssueFields.remove("comment");
        if(nonNull(commentField) && !(commentField.getJsonValue() instanceof JsonNull)){
            updateObject.add("comment", commentField.getJsonValue());
        }

        // Work Log
        FieldEditorInfo worklogField = createIssueFields.remove("worklog");
        if(nonNull(worklogField)) {
            JsonElement worklogFieldValue = worklogField.getJsonValue();
            if (!(worklogFieldValue instanceof JsonNull)) {
                updateObject.add("worklog", worklogFieldValue);
            }
        }

        // Linked Issues
        FieldEditorInfo issueLinkField = createIssueFields.remove("issuelinks");
        if(nonNull(issueLinkField) && !(issueLinkField.getJsonValue() instanceof JsonNull)){
            updateObject.add("issuelinks", issueLinkField.getJsonValue());
        }

        if(updateObject.size() > 0){
            createIssueObject.add("update", updateObject);
        }

        //Fields
        JsonObject fieldsObject = new JsonObject();
        createIssueFields.forEach((key, value) -> {
            JsonElement jsonValue = value.getJsonValue();
            if(!(jsonValue instanceof JsonNull)){
                fieldsObject.add(key, jsonValue);
            }
        });

        if(fieldsObject.size() > 0){
            createIssueObject.add("fields", fieldsObject);
        }

        return createIssueObject.toString();
    }



}

