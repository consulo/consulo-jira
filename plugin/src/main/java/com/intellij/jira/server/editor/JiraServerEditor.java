package com.intellij.jira.server.editor;

import com.intellij.jira.server.JiraServer;
import com.intellij.jira.ui.editors.Editor;
import consulo.project.Project;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.ValidationInfo;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class JiraServerEditor implements Editor {

    private final Project myProject;
    private final JiraServer myServer;
    private final boolean mySelectedServer;

    private BiConsumer<JiraServer, Boolean> myChangeListener;
    private Consumer<JiraServer> myChangeUrlListener;

    private JiraServerAuthEditor myServerAuthEditor;

    public JiraServerEditor(Project project, JiraServer server, boolean selected, BiConsumer<JiraServer, Boolean> changeListener, Consumer<JiraServer> changeUrlListener) {
        this.myProject = project;
        this.myServer = server;
        this.mySelectedServer = selected;
        this.myChangeListener = changeListener;
        this.myChangeUrlListener = changeUrlListener;
    }

    @Override
    public JComponent createPanel(){
        myServerAuthEditor = new DefaultJiraServerAuthEditor(myProject, myServer, mySelectedServer, myChangeListener, myChangeUrlListener);

        return FormBuilder.createFormBuilder()
                .addComponent(myServerAuthEditor.createPanel())
                .getPanel();
    }


    @Nullable
    public ValidationInfo validate() {
        return myServerAuthEditor.validate();
    }


}
