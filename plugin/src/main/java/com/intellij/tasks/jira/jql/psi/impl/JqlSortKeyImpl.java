package com.intellij.tasks.jira.jql.psi.impl;

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import com.intellij.tasks.jira.jql.JqlTokenTypes;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;
import com.intellij.tasks.jira.jql.psi.JqlSortKey;

import jakarta.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public class JqlSortKeyImpl extends JqlElementImpl implements JqlSortKey {
  public JqlSortKeyImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlSortKey(this);
  }

  @Nonnull
  @Override
  public String getFieldName() {
    PsiElement fieldNode = getFirstChild();
    assert fieldNode != null;
    return unescape(fieldNode.getText());
  }

  @Override
  public boolean isAscending() {
    PsiElement order = findChildByType(JqlTokenTypes.SORT_ORDERS);
    return order == null || order.getNode().getElementType() == JqlTokenTypes.ASC_KEYWORD;
  }
}
