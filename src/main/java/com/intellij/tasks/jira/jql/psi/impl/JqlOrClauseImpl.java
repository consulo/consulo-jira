package com.intellij.tasks.jira.jql.psi.impl;

import consulo.language.ast.ASTNode;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;
import com.intellij.tasks.jira.jql.psi.JqlOrClause;

import jakarta.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public class JqlOrClauseImpl extends JqlBinaryClauseImpl implements JqlOrClause {
  public JqlOrClauseImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlOrClause(this);
  }
}
