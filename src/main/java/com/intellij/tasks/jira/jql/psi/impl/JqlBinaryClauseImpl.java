package com.intellij.tasks.jira.jql.psi.impl;

import consulo.language.ast.ASTNode;
import consulo.language.psi.util.PsiTreeUtil;
import com.intellij.tasks.jira.jql.psi.JqlBinaryClause;
import com.intellij.tasks.jira.jql.psi.JqlClause;

import jakarta.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public abstract class JqlBinaryClauseImpl extends JqlElementImpl implements JqlBinaryClause {
  protected JqlBinaryClauseImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public JqlClause getLeftSubClause() {
    return findChildByClass(JqlClause.class);
  }

  @Override
  public JqlClause getRightSubClause() {
    return PsiTreeUtil.getNextSiblingOfType(getLeftSubClause(), JqlClause.class);
  }
}
