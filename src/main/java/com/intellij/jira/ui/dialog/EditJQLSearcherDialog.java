package com.intellij.jira.ui.dialog;

import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.rest.model.jql.JQLSearcherEditor;
import com.intellij.jira.tasks.RefreshIssuesTask;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static java.util.Objects.nonNull;

public class EditJQLSearcherDialog extends DialogWrapper {

    protected final Project myProject;
    protected JQLSearcher mySearcher;
    private JQLSearcher myOldSearcher;
    protected final JQLSearcherEditor myEditor;
    protected boolean myApplyOkAction;

    public EditJQLSearcherDialog(@NotNull Project project, @NotNull JQLSearcher searcher) {
        this(project, searcher, true, true);
    }


    public EditJQLSearcherDialog(@NotNull Project project, @NotNull JQLSearcher searcher, boolean selected, boolean applyOkAction) {
        super(project, false);
        this.myProject = project;
        this.myOldSearcher = searcher.clone();
        this.mySearcher = searcher;
        this.myEditor = new JQLSearcherEditor(myProject, mySearcher, selected);
        this.myApplyOkAction = applyOkAction;

        setTitle("Edit JQL Searcher");
        init();
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return myEditor.createPanel();
    }



    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return myEditor.validate();
    }

    @Override
    protected void doOKAction() {
        myEditor.apply();
        if(myApplyOkAction && nonNull(myProject)){
            JQLSearcherManager jqlManager = getJqlSearcherManager();
            mySearcher.setShared(myEditor.isSharedSearcher());
            jqlManager.update(myProject, myOldSearcher.getAlias(), mySearcher, myEditor.isSelectedSearcher());
            if(myApplyOkAction){
                new RefreshIssuesTask(myProject).queue();
            }

        }

        super.doOKAction();
    }


    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return myEditor.getAliasField();
    }


    public JQLSearcherManager getJqlSearcherManager(){
        return JQLSearcherManager.getInstance();
    }


    public JQLSearcher getJqlSearcher(){
        return mySearcher;
    }

    public boolean isSelectedSearcher(){
        return myEditor.isSelectedSearcher();
    }

    public boolean isSharedSearcher(){
        return myEditor.isSharedSearcher();
    }
}
