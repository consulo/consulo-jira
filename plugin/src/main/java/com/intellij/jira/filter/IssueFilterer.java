package com.intellij.jira.filter;

import com.intellij.jira.data.Issues;
import com.intellij.jira.filter.assignee.AssigneeFilter;
import com.intellij.jira.filter.priority.PriorityFilter;
import com.intellij.jira.filter.status.StatusFilter;
import com.intellij.jira.filter.type.TypeFilter;
import com.intellij.jira.rest.model.JiraIssue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_ASSIGNEE_FILTER;
import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_PRIORITY_FILTER;
import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_STATUS_FILTER;
import static com.intellij.jira.filter.IssueFilterCollectionImpl.ISSUE_TYPE_FILTER;

public class IssueFilterer {

    public Issues filter(Issues issues, IssueFilterCollection filters) {
        List<JiraIssue> filteredIssues = new ArrayList<>(issues.get());
        TypeFilter typeFilter = filters.get(ISSUE_TYPE_FILTER);
        Collection<String> issueTypes = typeFilter.getIssueTypes();
        if (!issueTypes.isEmpty()) {
            filteredIssues = filteredIssues.stream().filter(issue -> typeFilter.matches(issue.getIssuetype())).collect(Collectors.toList());
        }

        StatusFilter statusFilter = filters.get(ISSUE_STATUS_FILTER);
        Collection<String> issueStatus = statusFilter.getIssueStatus();
        if (!issueStatus.isEmpty()) {
            filteredIssues = filteredIssues.stream().filter(issue -> statusFilter.matches(issue.getStatus())).collect(Collectors.toList());
        }

        PriorityFilter priorityFilter = filters.get(ISSUE_PRIORITY_FILTER);
        Collection<String> priorities = priorityFilter.getPriorities();
        if (!priorities.isEmpty()) {
            filteredIssues = filteredIssues.stream().filter(issue -> priorityFilter.matches(issue.getPriority())).collect(Collectors.toList());
        }

        AssigneeFilter assigneeFilter = filters.get(ISSUE_ASSIGNEE_FILTER);
        Collection<String> users = assigneeFilter.getUsers();
        if (!users.isEmpty()) {
            filteredIssues = filteredIssues.stream().filter(assigneeFilter::matches).collect(Collectors.toList());
        }

        return Issues.of(filteredIssues);
    }

}
