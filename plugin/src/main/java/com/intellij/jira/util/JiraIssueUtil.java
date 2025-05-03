package com.intellij.jira.util;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueUser;
import consulo.application.util.DateFormatUtil;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nonnull;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.jira.util.JiraLabelUtil.EMPTY_TEXT;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class JiraIssueUtil {

    private static final Pattern BODY_NAME_PATTERN = Pattern.compile("(\\[~(\\w+)])");

    private JiraIssueUtil() { }

    @Nonnull
    public static String getAssignee(@Nonnull JiraIssue jiraIssue) {
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

    @Nonnull
    public static String getIssueType(@Nonnull JiraIssue jiraIssue) {
        return nonNull(jiraIssue.getIssuetype()) ? jiraIssue.getIssuetype().getName() : EMPTY_TEXT;
    }

    @Nonnull
    public static String getPriority(@Nonnull JiraIssue jiraIssue) {
        return nonNull(jiraIssue.getPriority()) ? jiraIssue.getPriority().getName() : EMPTY_TEXT;
    }

    public static String getStatus(@Nonnull JiraIssue jiraIssue) {
        return jiraIssue.getStatus().getName();
    }

    public static String getCreated(@Nonnull JiraIssue jiraIssue) {
        return getPrettyDateTime(jiraIssue.getCreated());
    }

    public static String getUpdated(@Nonnull JiraIssue jiraIssue) {
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
