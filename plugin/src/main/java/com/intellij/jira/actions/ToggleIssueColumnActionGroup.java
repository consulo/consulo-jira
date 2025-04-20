package com.intellij.jira.actions;

import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.ui.table.column.JiraIssueColumn;
import consulo.application.ApplicationManager;
import consulo.project.Project;
import consulo.ui.ex.action.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.jira.ui.table.column.JiraIssueColumnUtils.*;
import static java.util.Objects.isNull;


public class ToggleIssueColumnActionGroup extends DefaultActionGroup {

    public ToggleIssueColumnActionGroup() {
        super();
    }

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        List<AnAction> actions = new ArrayList<>();
        if (e != null) {
            actions.add(AnSeparator.create("Show Columns"));
        }

        getHideableColumns().forEach(column ->
            actions.add(new ToggleIssueColumnActionGroup.ToggleColumnAction(column))
        );

        return actions.toArray(AnAction.EMPTY_ARRAY);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getData(Project.KEY);
        if (isNull(project)|| !project.isInitialized() || project.isDisposed()) {
            e.getPresentation().setVisible(false);
        } else {
            JiraServerManager manager = ApplicationManager.getApplication().getInstance(JiraServerManager.class);
            e.getPresentation().setVisible(manager.hasJiraServerConfigured(project));
        }
    }

    private static final class ToggleColumnAction extends ToggleAction {
        private final JiraIssueColumn<?, ?> myColumn;

        private ToggleColumnAction(@NotNull JiraIssueColumn<?, ?> column) {
            super(column.getName());
            myColumn = column;
        }

        @Override
        public boolean isSelected(@NotNull AnActionEvent e) {
            return isVisible(myColumn);
        }

        @Override
        public void setSelected(@NotNull AnActionEvent e, boolean state) {
            if (state) {
                addColumn(myColumn);
            } else {
                removeColumn(myColumn);
            }
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            super.update(e);
            e.getPresentation().setEnabledAndVisible(true);
        }

    }

}
