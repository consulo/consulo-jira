package com.intellij.tasks.jira.jql;

import consulo.language.ast.ASTNode;
import consulo.language.impl.psi.ASTWrapperPsiElement;

import jakarta.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public class JqlElement extends ASTWrapperPsiElement {
  public JqlElement(@Nonnull ASTNode node) {
    super(node);
  }
}
