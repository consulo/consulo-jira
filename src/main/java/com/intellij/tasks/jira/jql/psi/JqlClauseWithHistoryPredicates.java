package com.intellij.tasks.jira.jql.psi;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author Mikhail Golubev
 */
public interface JqlClauseWithHistoryPredicates extends JqlElement {
  @Nullable
  JqlOperand getAfter();

  @Nullable
  JqlOperand getBefore();

  @Nullable
  JqlOperand getOn();

  @Nullable
  JqlOperand getBy();

  @Nullable
  JqlOperand getDuring();

  @Nullable
  JqlOperand getFrom();

  @Nullable
  JqlOperand getTo();

  @Nonnull
  JqlHistoryPredicate[] getHistoryPredicates();
}
