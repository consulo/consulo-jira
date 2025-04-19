package com.intellij.jira.util;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueUser;
import consulo.application.util.DateFormatUtil;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.jira.util.JiraLabelUtil.EMPTY_TEXT;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class JiraIssueUtil {

    private static final Pattern BODY_NAME_PATTERN = Pattern.compile("(\\[~(\\w+)])");

    private JiraIssueUtil() { }

    @NotNull
    public static String getAssignee(@NotNull JiraIssue jiraIssue) {
        JiraIssueUser assignedUser = jiraIssue.getAssignee();
        if (isNull(assignedUser)) {
            return EMPTY_TEXT;
        }

        if (nonNull(assignedUser.getEmailAddress())) {
            String emailAddress = assignedUser.getEmailAddress();
            int index = emailAddress.lastIndexOf('@');

            return index >= 0 ? emailAddress.substring(0, index) : EMPTY_TEXT;
        }

        return StringUtil.defaultIfEmpty(assignedUser.getDisplayName(), EMPTY_TEXT);
    }

    @NotNull
    public static String getIssueType(@NotNull JiraIssue jiraIssue) {
        return nonNull(jiraIssue.getIssuetype()) ? jiraIssue.getIssuetype().getName() : EMPTY_TEXT;
    }

    @NotNull
    public static String getPriority(@NotNull JiraIssue jiraIssue) {
        return nonNull(jiraIssue.getPriority()) ? jiraIssue.getPriority().getName() : EMPTY_TEXT;
    }

    public static String getStatus(@NotNull JiraIssue jiraIssue) {
        return jiraIssue.getStatus().getName();
    }

    public static String getCreated(@NotNull JiraIssue jiraIssue) {
        return getPrettyDateTime(jiraIssue.getCreated());
    }

    public static String getUpdated(@NotNull JiraIssue jiraIssue) {
        return getPrettyDateTime(jiraIssue.getUpdated());
    }

    public static String getPrettyBody(String body){
        if(isNull(body)){
            return EMPTY_TEXT;
        }

        Matcher m = BODY_NAME_PATTERN.matcher(body);
        if(m.find()){
            body = body.replace(m.group(1), m.group(2));
        }

        return body;
    }

    public static String escapeComment(String body){
        return body.replaceAll("\r\n", "\\\\n");
    }

    public static String getPrettyDateTime(Date date){
        return nonNull(date) ? DateFormatUtil.formatPrettyDateTime(date) : EMPTY_TEXT;
    }

}
