package com.intellij.jira.ui.model;

import com.intellij.jira.rest.model.JiraIssueWorklog;

import javax.swing.*;
import java.util.List;

public class JiraIssueWorklogListModel extends AbstractListModel<JiraIssueWorklog> {

    private final List<JiraIssueWorklog> worklogs;

    public JiraIssueWorklogListModel(List<JiraIssueWorklog> worklogs) {
        this.worklogs = worklogs;
    }

    @Override
    public int getSize() {
        return worklogs.size();
    }

    @Override
    public JiraIssueWorklog getElementAt(int index) {
        return worklogs.get(index);
    }
}
