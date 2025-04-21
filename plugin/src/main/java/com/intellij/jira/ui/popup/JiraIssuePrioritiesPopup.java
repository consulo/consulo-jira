package com.intellij.jira.ui.popup;

import consulo.dataContext.DataContext;
import consulo.project.Project;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.awt.UIExAWTDataKey;
import consulo.ui.ex.popup.JBPopupFactory;
import consulo.ui.ex.popup.ListPopup;
import consulo.util.lang.function.Predicates;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JiraIssuePrioritiesPopup {

    public static ListPopup create(@NotNull ActionGroup actionGroup, @NotNull Project project, @Nonnull JComponent component) {
        return JBPopupFactory.getInstance().createActionGroupPopup("Change Priority To",
            actionGroup,
            DataContext.builder().add(Project.KEY, project).add(UIExAWTDataKey.CONTEXT_COMPONENT, component).build(),
            false,
            false,
            false,
            null,
            10,
            Predicates.alwaysTrue()
        );
    }
}
