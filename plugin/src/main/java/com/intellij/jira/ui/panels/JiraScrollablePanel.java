package com.intellij.jira.ui.panels;

import com.intellij.jira.util.JiraBorders;
import consulo.jira.impl.ui.ScrollablePanel;
import consulo.ui.ex.awt.VerticalFlowLayout;

import java.awt.*;

import static com.intellij.jira.util.JiraLabelUtil.WHITE;

public class JiraScrollablePanel extends ScrollablePanel {

    public JiraScrollablePanel() {
        super(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
        setOpaque(false);
        setBorder(JiraBorders.empty(5));
    }

    @Override
    public Color getBackground() {
        return WHITE;
    }

}
