package com.intellij.jira.ui;

import com.intellij.jira.JiraTabsManager;
import consulo.project.Project;
import consulo.project.ui.wm.ToolWindowFactory;
import consulo.ui.ex.toolWindow.ToolWindow;
import consulo.ui.ex.toolWindow.ToolWindowType;
import org.jetbrains.annotations.NotNull;

public class JiraToolWindowFactory implements ToolWindowFactory {

    public static final String TOOL_WINDOW_ID = "Jira";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JiraTabsManager.getInstance(project).openIssuesTab();
        toolWindow.setType(ToolWindowType.DOCKED, null);
    }

}
