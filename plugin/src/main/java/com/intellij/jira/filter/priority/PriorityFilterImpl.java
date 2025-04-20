package com.intellij.jira.filter.priority;

import com.intellij.jira.rest.model.JiraIssuePriority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PriorityFilterImpl implements PriorityFilter {

    private final List<String> myPriorities;

    public PriorityFilterImpl() {
        myPriorities = new ArrayList<>();
    }

    public PriorityFilterImpl(List<String> values) {
        myPriorities = values;
    }

    @Override
    public Collection<String> getPriorities() {
        return myPriorities;
    }

    @Override
    public boolean matches(JiraIssuePriority priority) {
        return getPriorities().contains(priority.getName());
    }

}
