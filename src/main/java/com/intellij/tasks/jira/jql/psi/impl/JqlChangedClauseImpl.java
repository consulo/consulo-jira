package com.intellij.tasks.jira.jql.psi.impl;

import consulo.language.ast.ASTNode;
import com.intellij.tasks.jira.jql.psi.JqlChangedClause;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;

import jakarta.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public class JqlChangedClauseImpl extends JqlClauseWithHistoryPredicatesImpl implements JqlChangedClause {
  public JqlChangedClauseImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlChangedClause(this);
  }

  @Nonnull
  @Override
  public Type getType() {
    return Type.CHANGED;
  }
}
