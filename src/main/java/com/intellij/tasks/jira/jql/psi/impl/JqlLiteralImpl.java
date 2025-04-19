package com.intellij.tasks.jira.jql.psi.impl;

import consulo.language.ast.ASTNode;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;
import com.intellij.tasks.jira.jql.psi.JqlLiteral;

import jakarta.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public class JqlLiteralImpl extends JqlElementImpl implements JqlLiteral {
  public JqlLiteralImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlLiteral(this);
  }

  @Override
  public String getEscapedText() {
    return unescape(getText());
  }

  @Override
  public String getOriginalText() {
    return getNode().getText();
  }
}
