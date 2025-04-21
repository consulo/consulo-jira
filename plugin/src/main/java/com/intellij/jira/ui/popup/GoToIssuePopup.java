package com.intellij.jira.ui.popup;

import consulo.application.ApplicationManager;
import consulo.language.editor.ui.awt.TextFieldWithAutoCompletion;
import consulo.project.Project;
import consulo.ui.ex.awt.JBLabel;
import consulo.ui.ex.awt.JBUI;
import consulo.ui.ex.awt.UIUtil;
import consulo.ui.ex.popup.JBPopup;
import consulo.ui.ex.popup.JBPopupFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.function.Function;

public class GoToIssuePopup {

    private TextFieldWithAutoCompletion textField;
    private JBPopup popup;
    private Function<String, Future> onSelectedIssueKey;


    public GoToIssuePopup(@NotNull Project project, Collection<String> values, Function<String, Future> onSelectedIssueKey) {
        this.onSelectedIssueKey = onSelectedIssueKey;
        TextFieldWithAutoCompletion.StringsCompletionProvider provider = new TextFieldWithAutoCompletion.StringsCompletionProvider(values, null);

        textField = new TextFieldWithAutoCompletion<>(project, provider, true, ""){

            @Override
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onSelectedIssueKey.apply(textField.getText());
                    closePopup();
                    return true;
                }
                return false;
            }
        };


        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setBorder(JBUI.Borders.empty(3));

        JBLabel label = new JBLabel("Enter issue key");
        label.setFont(UIUtil.getLabelFont().deriveFont(Font.BOLD));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
        panel.setLayout(layout);
        panel.add(label);
        panel.add(textField);
        panel.setBorder(JBUI.Borders.empty(2));

        popup = JBPopupFactory.getInstance().createComponentPopupBuilder(panel, textField)
                .setCancelOnClickOutside(true).setCancelOnWindowDeactivation(true).setCancelKeyEnabled(true)
                .setRequestFocus(true).createPopup();

    }


    public void show(Component component){
        popup.showInCenterOf(component);
    }

    public void closePopup(){
        ApplicationManager.getApplication().invokeLater(() -> popup.closeOk(null));
    }
}
