package com.intellij.tasks.jira.model;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Date;
import java.util.List;

/**
 * @author Mikhail Golubev
 */
public abstract class JiraIssue {
  public String toString() {
    return String.format("JiraIssue(key=%s, summary='%s')", getKey(), getSummary());
  }

  @Nonnull
  public abstract String getKey();

  @Nonnull
  public abstract String getIssueUrl();

  @Nonnull
  public abstract String getSummary();

  @Nullable
  public abstract String getDescription();

  @Nonnull
  public abstract Date getCreated();

  @Nonnull
  public abstract Date getUpdated();

  @Nullable
  public abstract Date getResolutionDate();

  @Nullable
  public abstract Date getDueDate();

  @Nonnull
  public abstract JiraIssueType getIssueType();

  @Nullable
  public abstract JiraUser getAssignee();

  @Nullable
  public abstract JiraUser getReporter();

  @Nonnull
  public abstract List<JiraComment> getComments();

  @Nonnull
  public abstract JiraStatus getStatus();
}
