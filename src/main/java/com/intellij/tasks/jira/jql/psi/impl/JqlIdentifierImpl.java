package com.intellij.tasks.jira.jql.psi.impl;

import jakarta.annotation.Nonnull;

import consulo.language.ast.ASTNode;
import com.intellij.tasks.jira.jql.JqlTokenTypes;
import com.intellij.tasks.jira.jql.psi.JqlElementVisitor;
import com.intellij.tasks.jira.jql.psi.JqlIdentifier;

/**
 * @author Mikhail Golubev
 */
public class JqlIdentifierImpl extends JqlElementImpl implements JqlIdentifier {
  public JqlIdentifierImpl(@Nonnull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(JqlElementVisitor visitor) {
    visitor.visitJqlIdentifier(this);
  }

  @Override
  public boolean isCustomField() {
    return findChildByType(JqlTokenTypes.CUSTOM_FIELD) != null;
  }

  @Override
  public String getEscapedText() {
    return unescape(getText());
  }
}
