package com.intellij.jira.ui.popup;

import consulo.application.ui.wm.IdeFocusManager;
import consulo.language.editor.CommonDataKeys;
import consulo.language.editor.PlatformDataKeys;
import consulo.project.Project;
import consulo.ui.ex.action.ActionGroup;
import consulo.util.lang.function.Conditions;
import org.jetbrains.annotations.NotNull;

public class JiraIssueAssignableUsersPopup extends PopupFactoryImpl.ActionGroupPopup {

    public JiraIssueAssignableUsersPopup(@NotNull ActionGroup actionGroup, @NotNull Project project) {
        super("Assign to", actionGroup, SimpleDataContext.builder()
                .add(CommonDataKeys.PROJECT, project)
                .add(PlatformDataKeys.CONTEXT_COMPONENT, IdeFocusManager.getInstance(project).getFocusOwner())
                .build(), false, false, false, false, null, 10, Conditions.alwaysTrue(), null);
    }

}
