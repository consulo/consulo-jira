package com.intellij.tasks.jira.jql.psi.impl;

import com.intellij.tasks.jira.jql.psi.JqlAndClause;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;
import consulo.language.ast.ASTNode;

import jakarta.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public class JqlAndClauseImpl extends JqlBinaryClauseImpl implements JqlAndClause {
  public JqlAndClauseImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlAndClause(this);
  }
}
