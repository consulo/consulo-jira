package com.intellij.tasks.jira.jql.psi.impl;

import jakarta.annotation.Nonnull;

import consulo.language.ast.ASTNode;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;
import com.intellij.tasks.jira.jql.psi.JqlList;
import com.intellij.tasks.jira.jql.psi.JqlOperand;

/**
 * @author Mikhail Golubev
 */
public class JqlListImpl extends JqlElementImpl implements JqlList {
  public JqlListImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlList(this);
  }

  @Override
  public JqlOperand[] getElements() {
    return findChildrenByClass(JqlOperand.class);
  }
}
