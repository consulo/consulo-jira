package com.intellij.jira.ui;

import consulo.ui.ex.awt.UIUtil;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;

import static com.intellij.jira.util.JiraLabelUtil.DACULA_DEFAULT_COLOR;
import static com.intellij.jira.util.JiraLabelUtil.WHITE;

public class JiraTextPane extends JTextPane {

    private static final HTMLEditorKit HTML_EDITOR_KIT = new HTMLEditorKit();
    private static final String HTML_CONTENT_TYPE = "text/html";

    static {
        String rule = UIUtil.displayPropertiesToCSS(UIUtil.getLabelFont(), UIUtil.getLabelForeground());
        HTML_EDITOR_KIT.getStyleSheet().addRule(rule);
    }

    public JiraTextPane() {
        this(false);
    }

    public JiraTextPane(boolean editable) {
        setEditorKit(HTML_EDITOR_KIT);
        setContentType(HTML_CONTENT_TYPE);
        setBackground(UIUtil.isUnderDarcula() ? DACULA_DEFAULT_COLOR : WHITE);
        setEditable(editable);
    }

    public void setHTMLText(String text) {
        setText("<html><body>"+ text +"</body></html>");
    }

}
