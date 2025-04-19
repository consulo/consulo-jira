package com.intellij.tasks.jira.jql.psi.impl;

import jakarta.annotation.Nonnull;

import consulo.language.ast.ASTNode;
import com.intellij.tasks.jira.jql.psi.*;

/**
 * @author Mikhail Golubev
 */
public class JqlFunctionCallImpl extends JqlElementImpl implements JqlFunctionCall {
  public JqlFunctionCallImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlFunctionCall(this);
  }

  @Nonnull
  @Override
  public JqlIdentifier getFunctionName() {
    JqlIdentifier idenifier = findChildByClass(JqlIdentifier.class);
    assert idenifier != null;
    return idenifier;
  }

  @Nonnull
  @Override
  public JqlArgumentList getArgumentList() {
    return findChildByClass(JqlArgumentList.class);
  }
}
