package com.intellij.jira.data;

import java.util.List;

public interface Getter<T> {

    T getIssue(String issueKey);

    List<T> getIssues(String jql);

}
