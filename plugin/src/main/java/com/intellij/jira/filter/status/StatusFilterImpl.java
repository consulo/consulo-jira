package com.intellij.jira.filter.status;

import com.intellij.jira.rest.model.JiraIssueStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StatusFilterImpl implements StatusFilter {

    private final List<String> myStatus;

    public StatusFilterImpl() {
        this(new ArrayList<>());
    }

    public StatusFilterImpl(List<String> statuses) {
        myStatus = statuses;
    }

    @Override
    public Collection<String> getIssueStatus() {
        return myStatus;
    }

    @Override
    public boolean matches(JiraIssueStatus status) {
        return getIssueStatus().contains(status.getName());
    }

}
