package com.intellij.jira.ui.labels;

import com.intellij.jira.rest.model.JiraIssueStatus;
import com.intellij.jira.util.JiraBorders;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.border.Border;
import java.awt.Font;

public class JiraStatusLightLabel extends JBLabel {

    protected static final Font SANS_SERIF_PLAIN = JBFont.create(new Font("SansSerif", Font.PLAIN, 9));
    private static final Font SANS_SERIF_BOLD = JBFont.create(new Font("SansSerif", Font.BOLD, 9));
    protected static final Border PADDING = JiraBorders.empty(1, 4, 0, 4);

    public JiraStatusLightLabel(@NotNull JiraIssueStatus status) {
        super(StringUtil.toUpperCase(status.getName()), LEFT);

        setFont(SANS_SERIF_BOLD);
        setBorder(JBUI.Borders.compound(JBUI.Borders.customLine(status.getCategoryColor(), 0, 2, 0, 0), PADDING));
        setForeground(status.getCategoryColor());
    }
}
