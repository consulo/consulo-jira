package com.intellij.jira.actions;

import com.intellij.jira.exceptions.InvalidPermissionException;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.JiraIssueActionPlaces;
import com.intellij.jira.ui.dialog.CreateIssueDialog;
import consulo.application.ApplicationManager;
import consulo.dataContext.DataContext;
import consulo.project.Project;
import consulo.ui.ex.action.ActionToolbar;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.Presentation;
import consulo.ui.ex.awt.AnActionButton;
import consulo.ui.ex.awt.BorderLayoutPanel;
import consulo.ui.ex.awt.JBUI;
import consulo.ui.ex.awt.action.CustomComponentAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.intellij.jira.rest.model.JiraPermissionType.BROWSE_PROJECTS;
import static com.intellij.jira.rest.model.JiraPermissionType.CREATE_ISSUES;
import static java.util.Objects.isNull;

public class CreateIssueButtonAction extends AnActionButton implements CustomComponentAction {

    public CreateIssueButtonAction() {

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(isNull(project)){
            return;
        }

        JiraServerManager manager = ApplicationManager.getApplication().getInstance(JiraServerManager.class);
        JiraRestApi jiraRestApi = manager.getJiraRestApi(project);
        if(isNull(jiraRestApi)) {
            return;
        }

        boolean hasPermission = jiraRestApi.userHasPermission(BROWSE_PROJECTS, CREATE_ISSUES);
        if(!hasPermission){
            throw new InvalidPermissionException("Jira", "You don't have permission to create issues");
        }

        CreateIssueDialog createIssueDialog = new CreateIssueDialog(project, jiraRestApi);
        createIssueDialog.show();
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        JButton createIssueButton = new JButton("Create Issue");
        int buttonHeight = JBUI.scale(26);
        createIssueButton.setPreferredSize(new Dimension(createIssueButton.getPreferredSize().width, buttonHeight));
        createIssueButton.setBorder(new DarculaButtonPainter() {
            @Override
            public Insets getBorderInsets(Component c) {
                return JBUI.emptyInsets();
            }});


        createIssueButton.setFocusable(false);
        createIssueButton.setEnabled(true);

        createIssueButton.addActionListener(e -> {
            DataContext dataContext = ActionToolbar.getDataContextFor(createIssueButton);
            actionPerformed(AnActionEvent.createFromAnAction(this, null, JiraIssueActionPlaces.JIRA_ISSUES_TOOLBAR_PLACE, dataContext));
        });

        JPanel panel = new BorderLayoutPanel().addToCenter(createIssueButton);
        panel.setBorder(JBUI.Borders.emptyLeft(6));

        return panel;
    }
}
