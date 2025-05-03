// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package com.intellij.tasks.jira.rest;

import com.intellij.tasks.jira.JiraTask;
import com.intellij.tasks.jira.model.JiraIssue;
import consulo.task.Comment;
import consulo.task.TaskRepository;
import consulo.task.TaskState;
import consulo.task.TaskType;
import consulo.util.collection.ContainerUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Date;

/**
 * @author Dmitry Avdeev
 */
public class JiraRestTask extends JiraTask {

    private final JiraIssue myJiraIssue;

    public JiraRestTask(JiraIssue jiraIssue, TaskRepository repository) {
        super(repository);
        myJiraIssue = jiraIssue;
    }

    @Override
    public @Nonnull String getId() {
        return myJiraIssue.getKey();
    }

    @Override
    public @Nonnull String getSummary() {
        return myJiraIssue.getSummary();
    }

    @Override
    public String getDescription() {
        return myJiraIssue.getDescription();
    }


    @Override
    @Nonnull
    public Comment [] getComments() {
        return ContainerUtil.map2Array(myJiraIssue.getComments(), Comment.class, comment -> new Comment() {

            @Override
            public String getText() {
                return comment.getBody();
            }

            @Override
            public String getAuthor() {
                return comment.getAuthor().getDisplayName();
            }

            @Override
            public Date getDate() {
                return comment.getCreated();
            }

            @Override
            public String toString() {
                return comment.getAuthor().getDisplayName();
            }
        });
    }

    @Override
    protected @Nullable String getIconUrl() {
        // iconUrl will be null in JIRA versions prior 5.x.x
        return myJiraIssue.getIssueType().getIconUrl();
    }

    @Override
    public @Nonnull TaskType getType() {
        return getTypeByName(myJiraIssue.getIssueType().getName());
    }

    @Override
    public TaskState getState() {
        return getStateById(Integer.parseInt(myJiraIssue.getStatus().getId()));
    }

    @Override
    public @Nullable Date getUpdated() {
        return myJiraIssue.getUpdated();
    }

    @Override
    public Date getCreated() {
        return myJiraIssue.getCreated();
    }

    public JiraIssue getJiraIssue() {
        return myJiraIssue;
    }
}
