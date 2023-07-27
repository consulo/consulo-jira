package com.intellij.jira.actions;

import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.ide.util.gotoByName.DefaultChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ListChooseByNameModel;
import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.components.JiraNotificationManager;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskManager;
import com.intellij.tasks.actions.GotoTaskAction;
import com.intellij.tasks.actions.OpenTaskDialog;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OpenIssueTaskAction extends AnAction {
    public static String ID = "Jira.Issue.OpenTask";

    public OpenIssueTaskAction() {
        super("Open Task", "Open existing task or create a new one", AllIcons.General.Add);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getProject();

        if (project == null) {
            return;
        }

        var issue = e.getRequiredData(JiraDataKeys.ISSUE);

        var taskManager = TaskManager.getManager(project);

        var task = taskManager.findTask(issue.getKey());

        if (task != null) {
            taskManager.activateTask(task, false);
        } else {
            var issues = taskManager.getIssues(issue.getKey());

            if (issues.isEmpty()) {
                Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotification("No issue found", ""));
            } else if (issues.size() == 1) {
                showOpenTaskDialog(project, issues.get(0));
            } else {
                // when GotoTaskAction is performed, it takes initial search text from the already existing
                // ChooseByNamePopup popup, if any, retrieved from the project by CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY key
                // so, to filter tasks by issue key, we artificially create such popup and set predefinedText for it
                // using default values (placeholders) for all other constructor arguments
                var popup = new ChooseByNamePopup(
                        project,
                        new ListChooseByNameModel<>(project, "", "", List.of()), // placeholder
                        new DefaultChooseByNameItemProvider(null), // placeholder
                        null, // default
                        issue.getKey(), // only this argument matters
                        false, // default
                        0 // default
                ) {};

                project.putUserData(ChooseByNamePopup.CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY, popup);

                var action = ActionManager.getInstance().getAction(GotoTaskAction.ID);

                action.actionPerformed(e); // GotoTaskAction retrieves only the project from AnActionEvent
            }
        }
    }

    /**
     * Copy-pasted from {@link GotoTaskAction#showOpenTaskDialog(Project, Task)}
     */
    private static void showOpenTaskDialog(Project project, Task task) {
        JBPopup hint = DocumentationManager.getInstance(project).getDocInfoHint();
        if (hint != null) hint.cancel();
        ApplicationManager.getApplication().invokeLater(() -> new OpenTaskDialog(project, task).show());
    }
}
