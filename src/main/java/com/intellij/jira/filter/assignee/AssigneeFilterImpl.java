package com.intellij.jira.filter.assignee;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.util.JiraIssueUtil;
import consulo.util.lang.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AssigneeFilterImpl implements AssigneeFilter {

    private final List<String> myUsers;

    public AssigneeFilterImpl() {
        myUsers = new ArrayList<>();
    }

    public AssigneeFilterImpl(List<String> values) {
        myUsers = values;
    }

    @Override
    public Collection<String> getUsers() {
        return myUsers;
    }

    @Override
    public boolean matches(JiraIssue issue) {
        String user = JiraIssueUtil.getAssignee(issue);
        return getUsers().contains(StringUtil.isEmpty(user) ? "Unassigned" : user);
    }
}
