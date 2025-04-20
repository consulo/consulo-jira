package com.intellij.tasks.jira.jql.psi.impl;

import consulo.language.ast.ASTNode;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;
import com.intellij.tasks.jira.jql.psi.JqlEmptyValue;
import jakarta.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public class JqlEmptyValueImpl extends JqlElementImpl implements JqlEmptyValue {
  public JqlEmptyValueImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitEmptyValue(this);
  }
}
