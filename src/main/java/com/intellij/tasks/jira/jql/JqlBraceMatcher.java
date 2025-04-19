package com.intellij.tasks.jira.jql;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.BracePair;
import consulo.language.Language;
import consulo.language.PairedBraceMatcher;

import jakarta.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
@ExtensionImpl
public class JqlBraceMatcher implements PairedBraceMatcher {
  private static final BracePair[] PAIRS = {new BracePair(JqlTokenTypes.LPAR, JqlTokenTypes.RPAR, false)};

  @Override
  public BracePair[] getPairs() {
    return PAIRS;
  }

  @Nonnull
  @Override
  public Language getLanguage() {
    return JqlLanguage.INSTANCE;
  }
}
