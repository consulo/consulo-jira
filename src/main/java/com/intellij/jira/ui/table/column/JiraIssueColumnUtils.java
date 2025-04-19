package com.intellij.jira.ui.table.column;

import com.intellij.jira.ui.JiraIssueUiProperties;
import consulo.application.ApplicationManager;

import java.util.List;
import java.util.stream.Collectors;

public final class JiraIssueColumnUtils {

    private JiraIssueColumnUtils() { }

    public static List<JiraIssueColumn<?, ?>> getVisibleColumns() {
        return getJiraIssueColumnManager().getCurrentColumns().stream()
                .filter(JiraIssueColumnUtils::isVisible)
                .collect(Collectors.toList());
    }

    public static List<JiraIssueColumn<?, ?>> getHideableColumns() {
        return getJiraIssueColumnManager().getHideableColumns();
    }

    public static void addColumn(JiraIssueColumn<?, ?> column) {
        JiraIssueColumnProperties columnProperties = getColumnProperties(column);
        changeVisibility(columnProperties, true);
    }

    public static void removeColumn(JiraIssueColumn<?, ?> column) {
        JiraIssueColumnProperties columnProperties = getColumnProperties(column);
        changeVisibility(columnProperties, false);
    }

    public static boolean isVisible(JiraIssueColumn<?, ?> column) {
        JiraIssueColumnProperties columnProperties = getColumnProperties(column);
        return getPropertyValue(columnProperties.getVisibility(), true);
    }

    private static void changeVisibility(JiraIssueColumnProperties columnProperties, boolean value) {
        changePropertyValue(columnProperties.getVisibility(), value);
    }

    private static <T> void changePropertyValue(JiraIssueUiProperties.JiraIssueUiProperty<T> property, T value) {
        if (exists(property) && get(property) != value) {
            set(property, value);
        }
    }

    private static <T> T getPropertyValue(JiraIssueUiProperties.JiraIssueUiProperty<T> property, T defaultValue) {
        if (exists(property)) {
            return get(property);
        }

        return defaultValue;
    }

    private static <T> T get(JiraIssueUiProperties.JiraIssueUiProperty<T> property) {
        return getJiraIssueApplicationSettings().get(property);
    }

    private static <T> void set(JiraIssueUiProperties.JiraIssueUiProperty<T> property, T value) {
        getJiraIssueApplicationSettings().set(property, value);
    }

    private static boolean exists(JiraIssueUiProperties.JiraIssueUiProperty<?> property) {
        return getJiraIssueApplicationSettings().exists(property);
    }

    private static JiraIssueColumnProperties getColumnProperties(JiraIssueColumn<?, ?> column) {
        return getJiraIssueColumnManager().getColumnProperties(column);
    }

    private static JiraIssueColumnManager getJiraIssueColumnManager() {
        return JiraIssueColumnManager.getInstance();
    }

    private static JiraIssueApplicationSettings getJiraIssueApplicationSettings() {
        return ApplicationManager.getApplication().getService(JiraIssueApplicationSettings.class);
    }

}
