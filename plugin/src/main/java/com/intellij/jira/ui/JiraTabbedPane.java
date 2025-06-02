package com.intellij.jira.ui;

import consulo.ui.ex.awt.JBTabbedPane;
import consulo.ui.ex.awt.JBUI;

public class JiraTabbedPane extends JBTabbedPane {

    public JiraTabbedPane(int tabPlacement) {
        super(TOP);
        setTabComponentInsets(JBUI.insets(0));
    }

}
