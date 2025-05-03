package com.intellij.jira.ui.labels;

import com.intellij.jira.rest.model.JiraIssueStatus;
import com.intellij.jira.util.JiraLabelUtil;
import consulo.ui.ex.awt.JBUI;
import jakarta.annotation.Nonnull;

import static com.intellij.jira.util.JiraLabelUtil.IN_PROGRESS_TEXT_COLOR;

public class JiraStatusDarkLabel extends JiraStatusLightLabel {

    public JiraStatusDarkLabel(@Nonnull JiraIssueStatus status) {
        super(status);

        setFont(SANS_SERIF_PLAIN);
        setBorder(JBUI.Borders.empty(4, 5));
        setBackground(status.getCategoryColor());
        setForeground(status.isInProgressCategory() ? IN_PROGRESS_TEXT_COLOR : JiraLabelUtil.WHITE);
        setOpaque(true);
    }
}
