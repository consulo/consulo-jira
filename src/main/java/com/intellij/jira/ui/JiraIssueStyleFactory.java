package com.intellij.jira.ui;

import com.intellij.jira.ui.highlighters.JiraIssueHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collection;

public final class JiraIssueStyleFactory {

    private JiraIssueStyleFactory() { }

    public static JiraIssueHighlighter.JiraIssueStyle foreground(Color foreground) {
        return create(foreground, null, null);
    }

    public static JiraIssueHighlighter.JiraIssueStyle background(Color background) {
        return create(null, background, null);
    }

    public static JiraIssueHighlighter.JiraIssueStyle bold() {
        return create(null, null, JiraIssueHighlighter.TextStyle.BOLD);
    }

    public static JiraIssueHighlighter.JiraIssueStyle create(Color myFgColor,
                                                             Color myBgColor,
                                                             JiraIssueHighlighter.TextStyle myTextStyle) {
        return new JiraIssueStyleImpl(myFgColor, myBgColor, myTextStyle);
    }

    @NotNull
    public static JiraIssueHighlighter.JiraIssueStyle combine(@NotNull Collection<? extends JiraIssueHighlighter.JiraIssueStyle> styles) {
        Color foreground = null;
        Color background = null;
        JiraIssueHighlighter.TextStyle textStyle = null;

        for (JiraIssueHighlighter.JiraIssueStyle style : styles) {
            if (foreground == null) {
                foreground = style.getForeground();
            }
            if (background == null) {
                background = style.getBackground();
            }
            if (textStyle == null) {
                textStyle = style.getTextStyle();
            }
            if (background != null && foreground != null && textStyle != null) break;
        }

        return create(foreground, background, textStyle);
    }


    private static class JiraIssueStyleImpl implements JiraIssueHighlighter.JiraIssueStyle {

        private final Color myFgColor;
        private final Color myBgColor;
        private final JiraIssueHighlighter.TextStyle myTextStyle;

        private JiraIssueStyleImpl(Color myFgColor, Color myBgColor, JiraIssueHighlighter.TextStyle myTextStyle) {
            this.myFgColor = myFgColor;
            this.myBgColor = myBgColor;
            this.myTextStyle = myTextStyle;
        }

        @Override
        public @Nullable Color getForeground() {
            return myFgColor;
        }

        @Override
        public @Nullable Color getBackground() {
            return myBgColor;
        }

        @Override
        public JiraIssueHighlighter.TextStyle getTextStyle() {
            return myTextStyle;
        }
    }


}
