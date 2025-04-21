package com.intellij.jira.ui.labels;

import com.intellij.jira.rest.model.JiraIssueStatus;
import com.intellij.jira.util.JiraBorders;
import consulo.ui.ex.awt.JBFont;
import consulo.ui.ex.awt.JBLabel;
import consulo.ui.ex.awt.JBUI;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

public class JiraStatusLightLabel extends JBLabel {

    protected static final Font SANS_SERIF_PLAIN = JBFont.create(new Font("SansSerif", Font.PLAIN, 9));
    private static final Font SANS_SERIF_BOLD = JBFont.create(new Font("SansSerif", Font.BOLD, 9));
    protected static final Border PADDING = JiraBorders.empty(1, 4, 0, 4);

    public JiraStatusLightLabel(@NotNull JiraIssueStatus status) {
        super(StringUtil.toUpperCase(status.getName()), LEFT);

        setFont(SANS_SERIF_BOLD);
        setBorder(new CompoundBorder(JBUI.Borders.customLine(status.getCategoryColor(), 0, 2, 0, 0), PADDING));
        setForeground(status.getCategoryColor());
    }
}
