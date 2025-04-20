package com.intellij.jira.ui.highlighters;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraIssueStyleFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public interface JiraIssueHighlighter {

    @NotNull
    JiraIssueStyle getStyle(@NotNull JiraIssue issue);


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
