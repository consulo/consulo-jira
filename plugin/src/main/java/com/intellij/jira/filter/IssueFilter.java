package com.intellij.jira.filter;

public interface IssueFilter {

    IssueFilterCollection.FilterKey<?> getKey();

    String getDisplayText();

}
