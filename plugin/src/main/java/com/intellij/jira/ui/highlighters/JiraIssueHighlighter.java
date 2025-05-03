package com.intellij.jira.ui.highlighters;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraIssueStyleFactory;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.awt.*;

public interface JiraIssueHighlighter {

    @Nonnull
    JiraIssueStyle getStyle(@Nonnull JiraIssue issue);


    interface JiraIssueStyle {

        JiraIssueStyle DEFAULT = JiraIssueStyleFactory.create(null, null, null);

        /**
         * Foreground color for issue entry or null if unspecified.
         */
        @Nullable
        Color getForeground();

        /**
         * Background color for issue entry or null if unspecified.
         */
        @Nullable
        Color getBackground();

        @Nullable
        TextStyle getTextStyle();

    }

    enum TextStyle {
        NORMAL,
        BOLD
    }
}
