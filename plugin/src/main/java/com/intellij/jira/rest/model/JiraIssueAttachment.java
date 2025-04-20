package com.intellij.jira.rest.model;

import java.util.Date;

public class JiraIssueAttachment {

    private String id;
    private String self;
    private String filename;
    private JiraIssueUser author;
    private Date created;
    private int size;
    private String mimeType;
    private String content;

    public JiraIssueAttachment() { }

    public String getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public Date getCreated() {
        return created;
    }

    public String getAuthorName() {
        return author.getDisplayName();
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getContent() {
        return content;
    }
}
