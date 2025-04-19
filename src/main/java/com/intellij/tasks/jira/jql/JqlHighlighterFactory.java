package com.intellij.tasks.jira.jql;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.highlight.SingleLazyInstanceSyntaxHighlighterFactory;
import consulo.language.editor.highlight.SyntaxHighlighter;

import jakarta.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
@ExtensionImpl
public class JqlHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory {
  @Nonnull
  @Override
  protected SyntaxHighlighter createHighlighter() {
    return new JqlHighlighter();
  }

  @Nonnull
  @Override
  public Language getLanguage() {
    return JqlLanguage.INSTANCE;
  }
}
