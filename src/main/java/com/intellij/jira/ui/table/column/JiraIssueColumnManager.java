package com.intellij.jira.ui.table.column;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.application.Application;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
public class JiraIssueColumnManager {
    private static final List<JiraIssueColumn<?, ?>> defaultColumns = List.of(IssueType.INSTANCE, Priority.INSTANCE, Key.INSTANCE,
        Summary.INSTANCE, Assignee.INSTANCE, Status.INSTANCE,
        ProjectKey.INSTANCE, Created.INSTANCE);

    private final Map<Integer, JiraIssueColumn<?, ?>> currentColumns = new HashMap<>();
    private final Map<String, Integer> currentColumnIndexes = new HashMap<>();
    private final Map<String, String> maxColumnWidth = new HashMap<>();
    private final Map<JiraIssueColumn<?, ?>, JiraIssueColumnProperties> currentColumnProperties = new LinkedHashMap<>();

    public JiraIssueColumnManager() {
        defaultColumns.forEach(column -> {
            int index = currentColumnIndexes.size();
            currentColumns.put(index, column);
            currentColumnIndexes.put(column.getId(), index);
            currentColumnProperties.put(column, new JiraIssueColumnProperties(column));
        });
    }

    @NotNull
    public static JiraIssueColumnManager getInstance() {
        return Application.get().getInstance(JiraIssueColumnManager.class);
    }

    public int getColumnsCount() {
        return currentColumns.size();
    }

    public JiraIssueColumn<?, ?> getColumn(int columnIndex) {
        return currentColumns.get(columnIndex);
    }

    public Integer getColumnIndex(JiraIssueColumn<?, ?> column) {
        return currentColumnIndexes.get(column.getId());
    }

    public List<JiraIssueColumn<?, ?>> getCurrentColumns() {
        return new ArrayList<>(currentColumns.values());
    }

    public List<JiraIssueColumn<?, ?>> getHideableColumns() {
        return currentColumns.values().stream().filter(JiraIssueColumn::isHideable).collect(Collectors.toList());
    }

    public String getColumnWidth(String columnId) {
        return maxColumnWidth.getOrDefault(columnId, "");
    }

    public void setColumnWidth(String columnId, String value) {
        maxColumnWidth.put(columnId, value);
    }

    public JiraIssueColumnProperties getColumnProperties(JiraIssueColumn<?, ?> column) {
        return currentColumnProperties.get(column);
    }

}
