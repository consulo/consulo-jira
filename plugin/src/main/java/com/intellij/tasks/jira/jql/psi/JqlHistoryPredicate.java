package com.intellij.tasks.jira.jql.psi;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author Mikhail Golubev
 */
public interface JqlHistoryPredicate extends JqlElement {
  enum Type {
    FROM,
    TO,
    BEFORE,
    AFTER,
    BY,
    ON,
    DURING
  }

  @Nonnull
  Type getType();

  @Nullable
  JqlOperand getOperand();
}
