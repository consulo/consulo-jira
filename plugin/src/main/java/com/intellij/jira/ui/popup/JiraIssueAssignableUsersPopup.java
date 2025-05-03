package com.intellij.jira.ui.popup;

import consulo.dataContext.DataContext;
import consulo.project.Project;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.awt.UIExAWTDataKey;
import consulo.ui.ex.popup.JBPopupFactory;
import consulo.ui.ex.popup.ListPopup;
import consulo.util.lang.function.Predicates;
import jakarta.annotation.Nonnull;

import javax.swing.*;

public class JiraIssueAssignableUsersPopup {

    public static ListPopup create(@Nonnull ActionGroup actionGroup, @Nonnull Project project, @Nonnull JComponent component) {
        return JBPopupFactory.getInstance().createActionGroupPopup(
            "Assign to",
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
