package com.intellij.jira.ui;

import com.intellij.jira.JiraTabsManager;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.dumb.DumbAware;
import consulo.jira.icon.JiraIconGroup;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.project.ui.wm.ToolWindowFactory;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.toolWindow.ToolWindow;
import consulo.ui.ex.toolWindow.ToolWindowAnchor;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;

@ExtensionImpl
public class JiraToolWindowFactory implements ToolWindowFactory, DumbAware {

    public static final String TOOL_WINDOW_ID = "Jira";

    @Nonnull
    @Override
    public String getId() {
        return TOOL_WINDOW_ID;
    }

    @RequiredUIAccess
    @Override
    public void createToolWindowContent(@Nonnull Project project, @Nonnull ToolWindow toolWindow) {
        JiraTabsManager.getInstance(project).openIssuesTab();
    }

    @Nonnull
    @Override
    public ToolWindowAnchor getAnchor() {
        return ToolWindowAnchor.BOTTOM;
    }

    @Nonnull
    @Override
    public Image getIcon() {
        return JiraIconGroup.toolwindowjira();
    }

    @Nonnull
    @Override
    public LocalizeValue getDisplayName() {
        return LocalizeValue.localizeTODO(TOOL_WINDOW_ID);
    }

    @Override
    public boolean canCloseContents() {
        return true;
    }
}
