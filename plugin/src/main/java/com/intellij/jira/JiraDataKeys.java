package com.intellij.jira;

import com.intellij.jira.rest.model.*;
import consulo.util.dataholder.Key;

public class JiraDataKeys {

    private JiraDataKeys() { }

    public static final Key<JiraIssue> ISSUE = Key.create("issue");

    public static final Key<String> ISSUE_KEY = Key.create("issue.key");

    public static final Key<String> PROJECT_KEY = Key.create("project.key");

    public static final Key<JiraIssueComment> ISSUE_COMMENT = Key.create("issue.comment");

    public static final Key<JiraIssueAttachment> ISSUE_ATTACHMENT = Key.create("issue.attachment");

    public static final Key<JiraIssueLink> ISSUE_LINK = Key.create("issue.link");

    public static final Key<JiraIssueWorklog> ISSUE_WORKLOG = Key.create("issue.worklog");

    public static final Key<JiraIssueTimeTracking> ISSUE_TIME_TRACKING = Key.create("issue.timetracking");
}
