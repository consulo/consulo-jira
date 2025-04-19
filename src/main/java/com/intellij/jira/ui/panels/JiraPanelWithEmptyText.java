package com.intellij.jira.ui.panels;

import consulo.ui.ex.awt.JBPanelWithEmptyText;
import org.jetbrains.annotations.NotNull;

public class JiraPanelWithEmptyText extends JBPanelWithEmptyText {

    public JiraPanelWithEmptyText(@NotNull String emptyText) {
        super();
        getEmptyText().setText(emptyText);
    }
}
