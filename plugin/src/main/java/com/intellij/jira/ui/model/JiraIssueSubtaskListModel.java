package com.intellij.jira.ui.model;

import javax.swing.AbstractListModel;
import java.util.List;

import com.intellij.jira.rest.model.JiraIssue;

public class JiraIssueSubtaskListModel  extends AbstractListModel<JiraIssue> {

    private List<JiraIssue> issueSubtasks;

    public JiraIssueSubtaskListModel(List<JiraIssue> issueSubtasks) {
        this.issueSubtasks = issueSubtasks;
    }

    @Override
    public int getSize() {
        return issueSubtasks.size();
    }

    @Override
    public JiraIssue getElementAt(int index) {
        return issueSubtasks.get(index);
    }
}
