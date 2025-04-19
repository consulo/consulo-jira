package com.intellij.tasks.jira.jql.psi;

import jakarta.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public interface JqlArgumentList extends JqlElement {
  @Nonnull
  JqlLiteral[] getArguments();
}
