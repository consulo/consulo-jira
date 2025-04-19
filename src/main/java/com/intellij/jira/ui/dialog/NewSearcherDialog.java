package com.intellij.jira.ui.dialog;

import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import consulo.project.Project;
import consulo.ui.ex.awt.ValidationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.nonNull;

public class NewSearcherDialog extends EditSearcherDialog {

    public NewSearcherDialog(@NotNull Project project) {
        super(project, new JQLSearcher());
        setTitle("New Searcher");
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        ValidationInfo validationInfo = myEditor.validate();
        if (validationInfo == null) {
            String alias = myEditor.getAliasField().getText();
            JQLSearcher searcher = getJqlSearcherManager().findByAlias(myProject, alias);
            if (searcher != null) {
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
            jqlManager.add(myProject, mySearcher);
        }

        close(0);
    }

}
