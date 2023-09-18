package com.intellij.jira.ui.labels;

import com.intellij.jira.rest.model.JiraIssueStatus;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

import static com.intellij.jira.util.JiraLabelUtil.IN_PROGRESS_TEXT_COLOR;

public class JiraStatusDarkLabel extends JiraStatusLightLabel {

    public JiraStatusDarkLabel(@NotNull JiraIssueStatus status) {
        super(status);

        setFont(SANS_SERIF_PLAIN);
        setBorder(JBUI.Borders.empty(4, 5));
        setBackground(status.getCategoryColor());
        setForeground(status.isInProgressCategory() ? IN_PROGRESS_TEXT_COLOR : JiraLabelUtil.WHITE);
        setOpaque(true);
    }
}
