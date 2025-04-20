package com.intellij.jira.filter.type;

import com.intellij.jira.rest.model.JiraIssueType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TypeFilterImpl implements TypeFilter {

    private final List<String> myIssueTypes;

    public TypeFilterImpl() {
        this(new ArrayList<>());
    }

    public TypeFilterImpl(List<String> issueTypes) {
        myIssueTypes = issueTypes;
    }

    @Override
    public Collection<String> getIssueTypes() {
        return myIssueTypes;
    }

    @Override
    public boolean matches(JiraIssueType issueType) {
        return getIssueTypes().contains(issueType.getName());
    }
}
