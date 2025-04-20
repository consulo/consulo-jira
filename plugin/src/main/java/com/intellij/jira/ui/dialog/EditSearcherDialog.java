package com.intellij.jira.ui.dialog;

import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.rest.model.jql.SearcherEditor;
import consulo.project.Project;
import consulo.ui.ex.awt.DialogWrapper;
import consulo.ui.ex.awt.ValidationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static java.util.Objects.nonNull;

public class EditSearcherDialog extends DialogWrapper {

    protected final Project myProject;
    protected JQLSearcher mySearcher;
    private final JQLSearcher myOldSearcher;
    protected final SearcherEditor myEditor;

    public EditSearcherDialog(@NotNull Project project, @NotNull JQLSearcher searcher) {
        super(project, false);
        myProject = project;
        myOldSearcher = searcher.clone();
        mySearcher = searcher;
        myEditor = new SearcherEditor(myProject, mySearcher);

        setTitle("Edit Searcher");
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
        ValidationInfo validationInfo = myEditor.validate();
        if (validationInfo == null) {
            String alias = myEditor.getAliasField().getText();
            JQLSearcher searcher = getJqlSearcherManager().findByAlias(myProject, alias);
            if (searcher != null && !searcher.getId().equals(mySearcher.getId())) {
                return new ValidationInfo("Alias '" + alias + "' already exist");
            }
        }

        return validationInfo;
    }

    @Override
    protected void doOKAction() {
        myEditor.apply();
        if(nonNull(myProject)){
            JQLSearcherManager jqlManager = getJqlSearcherManager();
            mySearcher.setShared(myEditor.isSharedSearcher());
            jqlManager.update(myProject, myOldSearcher.getId(), mySearcher);
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

}
