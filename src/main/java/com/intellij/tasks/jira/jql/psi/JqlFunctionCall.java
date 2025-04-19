package com.intellij.tasks.jira.jql.psi;

import jakarta.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public interface JqlFunctionCall extends JqlOperand {
  @Nonnull
  JqlIdentifier getFunctionName();

  @Nonnull
  JqlArgumentList getArgumentList();
}
