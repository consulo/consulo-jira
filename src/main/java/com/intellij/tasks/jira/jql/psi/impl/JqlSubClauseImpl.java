package com.intellij.tasks.jira.jql.psi.impl;

import consulo.language.ast.ASTNode;
import com.intellij.tasks.jira.jql.psi.JqlClause;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;
import com.intellij.tasks.jira.jql.psi.JqlSubClause;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author Mikhail Golubev
 */
public class JqlSubClauseImpl extends JqlElementImpl implements JqlSubClause {
  public JqlSubClauseImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlSubClause(this);
  }

  @Nullable
  @Override
  public JqlClause getInnerClause() {
    return findChildByClass(JqlClause.class);
  }
}
