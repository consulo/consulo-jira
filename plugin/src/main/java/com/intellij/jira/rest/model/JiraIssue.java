package com.intellij.jira.rest.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.jira.rest.JiraIssueCommentsWrapper;
import com.intellij.jira.rest.JiraIssueWorklogsWrapper;
import com.intellij.jira.util.JiraGsonUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.intellij.jira.util.JiraIssueField.*;

public class JiraIssue {

    private String id;
    private String self;
    private String key;
    private JsonObject fields;
    private JsonObject renderedFields;

    public JiraIssue() { }

    public String getId() {
        return id;
    }

    public String getSelf() {
        return self;
    }

    public String getKey() {
        return key;
    }

    public String getSummary() {
        return JiraGsonUtil.getAsString(fields.get(SUMMARY));
    }

    public String getRenderedDescription() {
        return JiraGsonUtil.getAsString(renderedFields.get(DESCRIPTION));
    }

    public Date getCreated() {
        return JiraGsonUtil.getAsDate(fields.get(CREATED));
    }

    public Date getUpdated() {
        return JiraGsonUtil.getAsDate(fields.get(UPDATED));
    }

    public Date getResolutiondate() {
        return JiraGsonUtil.getAsDate(fields.get(RESOLUTION_DATE));
    }

    public Date getDuedate() {
        return JiraGsonUtil.getAsDate(fields.get(DUEDATE));
    }

    public JiraIssueType getIssuetype() {
        return JiraGsonUtil.getAs(fields.get(ISSUE_TYPE), JiraIssueType.class);
    }

    public JiraIssueStatus getStatus() {
        return JiraGsonUtil.getAs(fields.get(STATUS), JiraIssueStatus.class);
    }

    public JiraIssueResolution getResolution() {
        return JiraGsonUtil.getAs(fields.get(RESOLUTION), JiraIssueResolution.class);
    }

    public JiraIssuePriority getPriority() {
        return JiraGsonUtil.getAs(fields.get(PRIORITY), JiraIssuePriority.class);
    }

    public JiraIssueUser getAssignee() {
        return JiraGsonUtil.getAs(fields.get(ASSIGNEE), JiraIssueUser.class);
    }

    public JiraIssueUser getCreator() {
        return JiraGsonUtil.getAs(fields.get(CREATOR), JiraIssueUser.class);
    }

    public JiraIssueUser getReporter() {
        return JiraGsonUtil.getAs(fields.get(REPORTER), JiraIssueUser.class);
    }

    public JiraIssueCommentsWrapper getRenderedComments(){
        return JiraGsonUtil.getAs(renderedFields.get(COMMENT), JiraIssueCommentsWrapper.class);
    }

    public List<JiraIssueWorklog> getWorklogs(){
        JiraIssueWorklogsWrapper worklog = JiraGsonUtil.getAs(fields.get(WORKLOG), JiraIssueWorklogsWrapper.class);
        return Objects.nonNull(worklog) ? worklog.getWorklogs() : new ArrayList<>();
    }

    public JiraIssueTimeTracking getTimetracking(){
        return JiraGsonUtil.getAs(fields.get(TIME_TRACKING), JiraIssueTimeTracking.class);
    }

    public List<JiraIssueLink> getIssueLinks(){
        return JiraGsonUtil.getAsList(fields.get(ISSUE_LINKS), JiraIssueLink[].class);
    }

    public JiraProject getProject(){
        return JiraGsonUtil.getAs(fields.get(PROJECT), JiraProject.class);
    }

    public List<JiraProjectVersion> getVersions(){
        return JiraGsonUtil.getAsList(fields.get(VERSIONS), JiraProjectVersion[].class);
    }

    public List<JiraProjectVersion> getFixVersions(){
        return JiraGsonUtil.getAsList(fields.get(FIX_VERSIONS), JiraProjectVersion[].class);
    }

    public List<JiraIssueComponent> getComponents(){
        return JiraGsonUtil.getAsList(fields.get(COMPONENTS), JiraIssueComponent[].class);
    }

    public List<String> getLabels(){
        return JiraGsonUtil.getAsList(fields.get(LABELS), String[].class);
    }

    public String getAsString(String fieldName) {
        return JiraGsonUtil.getAsString(fields.get(fieldName));
    }

    public Date getAsDate(String fieldName) {
        return JiraGsonUtil.getAsDate(fields.get(fieldName));
    }

    public JiraIssueUser getAsJiraIssueUser(String fieldName) {
        return JiraGsonUtil.getAs(fields.get(fieldName), JiraIssueUser.class);
    }

    public JiraIssueWatches getWatches() {
        return JiraGsonUtil.getAs(fields.get(WATCHES), JiraIssueWatches.class);
    }

    public List<JiraIssueAttachment> getAttachments() {
        return JiraGsonUtil.getAsList(fields.get(ATTACHMENT), JiraIssueAttachment[].class);
    }

    public JsonElement getCustomfieldValue(String customId) {
        return fields.get("customfield_" + customId);
    }


    public List<JiraIssue> getSubtasks() {
        return JiraGsonUtil.getAsList(fields.get(SUBTASKS), JiraIssue[].class);
    }

    public String getUrl(){
        return self.replaceFirst("(/rest([\\w/]+))", "/browse/" + getKey());
    }

    public boolean isResolved(){
        return getStatus().isDoneCategory();
    }

    public boolean hasComponents(){
        return !getComponents().isEmpty();
    }

    public boolean hasLabels(){
        return !getLabels().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JiraIssue jiraIssue = (JiraIssue) o;
        return Objects.equals(id, jiraIssue.id) &&
                Objects.equals(key, jiraIssue.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key);
    }
}
