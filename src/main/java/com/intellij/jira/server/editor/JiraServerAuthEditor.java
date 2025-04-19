package com.intellij.jira.server.editor;

import com.intellij.jira.server.JiraServer;
import com.intellij.jira.tasks.TestJiraServerConnectionTask;
import com.intellij.jira.ui.editors.Editor;
import consulo.application.ApplicationManager;
import consulo.application.progress.ProgressManager;
import consulo.component.ProcessCanceledException;
import consulo.project.Project;
import consulo.ui.ex.awt.*;
import consulo.ui.ex.awt.event.DocumentAdapter;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.intellij.jira.util.JiraPanelUtil.MARGIN_BOTTOM;

public abstract class JiraServerAuthEditor implements Editor {

    protected static final int DEFAULT_WIDTH = 450;
    protected static final int DEFAULT_HEIGHT = 24;

    protected final Project myProject;
    protected JiraServer myServer;
    protected final boolean mySelectedServer;

    protected BiConsumer<JiraServer, Boolean> myChangeListener;
    protected Consumer<JiraServer> myChangeUrlListener;


    protected JLabel myUrlLabel;
    protected JTextField myUrlField;

    protected JCheckBox myDefaultServerCheckbox;
    protected JCheckBox mySharedCheckbox;

    protected JPanel myTestPanel;
    protected JButton myTestButton;

    public JiraServerAuthEditor(Project project, JiraServer server, boolean selected, BiConsumer<JiraServer, Boolean> changeListener, Consumer<JiraServer> changeUrlListener) {
        myProject = project;
        myServer = server;
        mySelectedServer = selected;
        myChangeListener = changeListener;
        myChangeUrlListener = changeUrlListener;
        init();
    }

    public void installListeners() {
        installListener(myUrlField);
        installListener(myDefaultServerCheckbox);
        installListener(myTestButton);

        mySharedCheckbox.addActionListener(e -> {
            myServer.setShared(mySharedCheckbox.isSelected());
        });

    }

    private void init() {
        myUrlLabel = new JBLabel("Server URL:", 4);
        myUrlField = new JBTextField();
        myUrlField.setText(myServer.getUrl());
        myUrlField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        myDefaultServerCheckbox = new JCheckBox("Set Default");
        myDefaultServerCheckbox.setBorder(JBUI.Borders.emptyRight(4));
        myDefaultServerCheckbox.setSelected(mySelectedServer);

        mySharedCheckbox = new JCheckBox("Shared");
        mySharedCheckbox.setBorder(JBUI.Borders.emptyRight(4));
        mySharedCheckbox.setSelected(myServer.isShared());

        myTestPanel = new JPanel(new BorderLayout());
        myTestPanel.setBorder(MARGIN_BOTTOM);
        myTestButton = new JButton("Test");
        myTestPanel.add(myTestButton, BorderLayout.EAST);
    }

    protected void installListener(JTextField textField) {
        textField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    apply();
                });

            }
        });
    }

    private void installListener(JCheckBox checkBox) {
        checkBox.addActionListener(e -> defaultServerChanged());
    }

    private void installListener(JButton button){
        button.addActionListener(event -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                TestJiraServerConnectionTask task = new TestJiraServerConnectionTask(myProject, myServer);
                ProgressManager.getInstance().run(task);
                Exception e = task.getException();
                if (e == null) {
                    Messages.showMessageDialog(myProject, "Connection is successful", "Connection", Messages.getInformationIcon());
                } else if (!(e instanceof ProcessCanceledException)) {
                    String message = e.getMessage();
                    if (e instanceof UnknownHostException) {
                        message = "Unknown host: " + message;
                    }

                    if (message == null) {
                        message = "Unknown error";
                    }

                    Messages.showErrorDialog(myProject, StringUtil.capitalize(message), "Error");
                }
            });
        });
    }

    protected void apply(){
        myChangeUrlListener.accept(myServer);
    }

    protected boolean isSharedServer() {
        return mySharedCheckbox.isSelected();
    }

    private void defaultServerChanged(){
        myChangeListener.accept(myServer, myDefaultServerCheckbox.isSelected());
    }

    @Nullable
    public ValidationInfo validate() {
        if (StringUtil.isEmpty(StringUtil.trim(myUrlField.getText()))) {
            return new ValidationInfo("Url is required.");
        }

        return null;
    }

}
