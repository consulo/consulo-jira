package com.intellij.jira.ui.panels;

import com.intellij.jira.ui.dialog.ConfigureJiraServersDialog;
import consulo.project.Project;
import consulo.ui.ex.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;

public class JiraServerNotConfiguredPanel extends JiraPanelWithEmptyText {

    public JiraServerNotConfiguredPanel(@NotNull Project project) {
        super("No Jira server found");
        getEmptyText().setText("Test");
        getEmptyText()
            .appendSecondaryText("Configure", SimpleTextAttributes.LINK_PLAIN_ATTRIBUTES, e -> new ConfigureJiraServersDialog(project).show());
    }
}
