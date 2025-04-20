package com.intellij.tasks.jira.jql.psi;

import jakarta.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public interface JqlSortKey extends JqlElement {
  @Nonnull
  String getFieldName();

  boolean isAscending();
}
