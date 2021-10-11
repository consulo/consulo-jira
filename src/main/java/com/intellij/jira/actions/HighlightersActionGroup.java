package com.intellij.jira.actions;

import com.intellij.jira.JiraUiDataKeys;
import com.intellij.jira.ui.JiraIssueUiProperties;
import com.intellij.jira.ui.highlighters.JiraIssueHighlighterFactory;
import com.intellij.jira.ui.highlighters.JiraIssueHighlighterProperty;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.extensions.ExtensionPointName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HighlightersActionGroup extends ActionGroup {

    public static final ExtensionPointName<JiraIssueHighlighterFactory> JIRA_ISSUE_HIGHLIGHTER_FACTORY_EP =
            ExtensionPointName.create("com.intellij.jira.issueHighlighterFactory");

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        List<AnAction> actions = new ArrayList<>();

        if (e != null) {
            actions.add(new Separator("Highlight"));
            for (JiraIssueHighlighterFactory factory : JIRA_ISSUE_HIGHLIGHTER_FACTORY_EP.getExtensionList()) {
                actions.add(new EnableHighlighterAction(factory));
            }
        }

        return actions.toArray(AnAction.EMPTY_ARRAY);
    }


    private static class EnableHighlighterAction extends ToggleAction {

        private final JiraIssueHighlighterFactory myFactory;

        public EnableHighlighterAction(JiraIssueHighlighterFactory factory) {
            super(factory.getTitle());
            myFactory = factory;
        }

        @Override
        public boolean isSelected(@NotNull AnActionEvent e) {
            JiraIssueUiProperties properties = e.getData(JiraUiDataKeys.JIRA_UI_PROPERTIES);
            if (properties == null || !properties.exists(getProperty())) return false;
            return properties.get(getProperty());
        }

        @Override
        public void setSelected(@NotNull AnActionEvent e, boolean state) {
            JiraIssueUiProperties properties = e.getData(JiraUiDataKeys.JIRA_UI_PROPERTIES);
            if (properties != null && properties.exists(getProperty())) {
                properties.set(getProperty(), state);
            }
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            JiraIssueUiProperties properties = e.getData(JiraUiDataKeys.JIRA_UI_PROPERTIES);
            e.getPresentation().setEnabledAndVisible(properties != null && properties.exists(getProperty()));

            super.update(e);
        }


        private JiraIssueHighlighterProperty getProperty() {
            return JiraIssueHighlighterProperty.get(myFactory.getId());
        }
    }

}
