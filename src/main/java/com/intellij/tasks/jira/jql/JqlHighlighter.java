package com.intellij.tasks.jira.jql;

import consulo.colorScheme.TextAttributesKey;
import consulo.language.editor.highlight.SyntaxHighlighterBase;
import consulo.language.lexer.Lexer;
import consulo.codeEditor.HighlighterColors;
import consulo.language.ast.IElementType;

import jakarta.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static consulo.codeEditor.DefaultLanguageHighlighterColors.*;

/**
 * @author Mikhail Golubev
 */
public class JqlHighlighter extends SyntaxHighlighterBase
{
  private static final Map<IElementType, TextAttributesKey> KEYS = new HashMap<IElementType, TextAttributesKey>();
  static {
    KEYS.put(JqlTokenTypes.STRING_LITERAL, STRING);
    KEYS.put(JqlTokenTypes.NUMBER_LITERAL, NUMBER);
    KEYS.put(JqlTokenTypes.COMMA, COMMA);
    KEYS.put(JqlTokenTypes.LPAR, PARENTHESES);
    KEYS.put(JqlTokenTypes.RPAR, PARENTHESES);
    fillMap(KEYS, JqlTokenTypes.KEYWORDS, KEYWORD);
    fillMap(KEYS, JqlTokenTypes.SIGN_OPERATORS, OPERATION_SIGN);

    KEYS.put(JqlTokenTypes.BAD_CHARACTER, HighlighterColors.BAD_CHARACTER);
  }

  @Nonnull
  @Override
  public Lexer getHighlightingLexer() {
    return new _JqlLexer();
  }

  @Nonnull
  @Override
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return pack(KEYS.get(tokenType));
  }
}
