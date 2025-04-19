package com.intellij.jira.rest.model.jql;

import com.intellij.jira.ui.editors.Editor;
import com.intellij.tasks.jira.jql.JqlLanguage;
import consulo.language.editor.ui.awt.EditorTextField;
import consulo.language.editor.ui.awt.LanguageTextField;
import consulo.project.Project;
import consulo.ui.ex.awt.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static consulo.util.lang.StringUtil.isEmpty;
import static consulo.util.lang.StringUtil.trim;

public class SearcherEditor implements Editor {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 24;

    private final Project myProject;
    private final JQLSearcher mySearcher;

    private boolean mySharedSearcher;

    private JBLabel myAliasLabel;
    private JBTextField myAliasField;

    private JBLabel mySearchLabel;
    private EditorTextField mySearchQueryField;

    private JCheckBox mySharedSearcherCheckBox;

    public SearcherEditor(@NotNull Project project, @NotNull JQLSearcher searcher) {
        myProject = project;
        mySearcher = searcher;
        mySharedSearcher = searcher.isShared();
    }

    public void apply(){
        mySearcher.setAlias(trim(myAliasField.getText()));
        mySearcher.setJql(trim(mySearchQueryField.getText()));
    }

    @Override
    public JComponent createPanel() {
        myAliasLabel = new JBLabel("Alias:", 4);
        myAliasField = new JBTextField(mySearcher.getAlias());
        myAliasField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        mySearchLabel = new JBLabel("Search:", 4);
        mySearchQueryField = new LanguageTextField(JqlLanguage.INSTANCE, myProject, mySearcher.getJql());
        mySearchQueryField.setPreferredSize(JBUI.size(DEFAULT_WIDTH, 30));

        mySharedSearcherCheckBox = new JCheckBox("Share");
        mySharedSearcherCheckBox.setBorder(JBUI.Borders.emptyRight(4));
        mySharedSearcherCheckBox.setSelected(mySharedSearcher);

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(myAliasLabel, myAliasField)
                .addLabeledComponent(mySearchLabel, mySearchQueryField)
                .addComponent(mySharedSearcherCheckBox)
                .getPanel();
    }

    @Nullable
    public ValidationInfo validate(){
        if(isEmpty(trim(myAliasField.getText()))){
            return new ValidationInfo("Alias field is required");
        }

        if(isEmpty(trim(mySearchQueryField.getText()))){
            return new ValidationInfo("JQL field is required");
        }

        return null;
    }

    public JBTextField getAliasField() {
        return myAliasField;
    }

    public boolean isSharedSearcher(){
        return mySharedSearcherCheckBox.isSelected();
    }
}