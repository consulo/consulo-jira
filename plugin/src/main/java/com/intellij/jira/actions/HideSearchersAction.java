package com.intellij.jira.actions;

import com.intellij.jira.JiraUiDataKeys;
import com.intellij.jira.ui.JiraIssueUiProperties;
import consulo.annotation.component.ActionImpl;
import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import jakarta.annotation.Nonnull;

import static com.intellij.jira.ui.SearcherIssuesUi.SHOW_SEARCHERS_PROPERTY;

@ActionImpl(id = "Jira.Issues.Hide.Searchers")
public class HideSearchersAction extends AnAction {
    public HideSearchersAction() {
        super(LocalizeValue.localizeTODO("Hide Searchers"), LocalizeValue.empty(), PlatformIconGroup.vcsArrow_left());
    }
    @Override
    public void actionPerformed(@Nonnull AnActionEvent e) {
        JiraIssueUiProperties properties = e.getRequiredData(JiraUiDataKeys.JIRA_UI_PROPERTIES);
        properties.set(SHOW_SEARCHERS_PROPERTY, false);
    }
}
