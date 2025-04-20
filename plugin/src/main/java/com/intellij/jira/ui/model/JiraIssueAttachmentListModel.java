package com.intellij.jira.ui.model;

import com.intellij.jira.rest.model.JiraIssueAttachment;

import javax.swing.*;
import java.util.List;

public class JiraIssueAttachmentListModel extends AbstractListModel<JiraIssueAttachment> {

    private List<JiraIssueAttachment> issueAttachments;

    public JiraIssueAttachmentListModel(List<JiraIssueAttachment> issueAttachments) {
        this.issueAttachments = issueAttachments;
    }

    @Override
    public int getSize() {
        return issueAttachments.size();
    }

    @Override
    public JiraIssueAttachment getElementAt(int index) {
        return issueAttachments.get(index);
    }

}
