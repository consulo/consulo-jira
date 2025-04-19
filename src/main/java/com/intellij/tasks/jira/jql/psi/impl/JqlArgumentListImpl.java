package com.intellij.tasks.jira.jql.psi.impl;

import jakarta.annotation.Nonnull;

import consulo.language.ast.ASTNode;
import com.intellij.tasks.jira.jql.psi.JqlArgumentList;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;
import com.intellij.tasks.jira.jql.psi.JqlLiteral;

/**
 * @author Mikhail Golubev
 */
public class JqlArgumentListImpl extends JqlElementImpl implements JqlArgumentList {
  public JqlArgumentListImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlArgumentList(this);
  }

  @Nonnull
  @Override
  public JqlLiteral[] getArguments() {
    return findChildrenByClass(JqlLiteral.class);
  }
}
