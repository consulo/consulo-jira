package com.intellij.tasks.jira.jql.psi;

import jakarta.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public interface JqlOrderBy extends JqlElement {
  @Nonnull
  JqlSortKey[] getSortKeys();
}
