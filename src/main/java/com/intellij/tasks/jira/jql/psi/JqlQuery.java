package com.intellij.tasks.jira.jql.psi;

import jakarta.annotation.Nullable;

/**
 * @author Mikhail Golubev
 */
public interface JqlQuery extends JqlElement {
  @Nullable
  JqlClause getClause();

  @Nullable
  JqlOrderBy getOrderBy();
}
