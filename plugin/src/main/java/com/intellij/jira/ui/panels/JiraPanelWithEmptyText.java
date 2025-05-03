package com.intellij.jira.ui.panels;

import consulo.ui.ex.awt.JBPanelWithEmptyText;
import jakarta.annotation.Nonnull;

public class JiraPanelWithEmptyText extends JBPanelWithEmptyText {

    public JiraPanelWithEmptyText(@Nonnull String emptyText) {
        super();
        getEmptyText().attachTo(this);
        getEmptyText().setText(emptyText);
    }
}
