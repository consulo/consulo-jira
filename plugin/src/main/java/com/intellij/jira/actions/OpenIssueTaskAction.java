package com.intellij.jira.actions;

import consulo.application.AllIcons;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import jakarta.annotation.Nonnull;

public class OpenIssueTaskAction extends AnAction {
    public static String ID = "Jira.Issue.OpenTask";

    public OpenIssueTaskAction() {
        super("Open Task", "Open existing task or create a new one", AllIcons.General.Add);
    }

    @Override
    public void actionPerformed(@Nonnull AnActionEvent e) {
        // TODO call GotoTaskAction
//        var project = e.getData(Project.KEY);
//
//        if (project == null) {
//            return;
//        }
//
//        var issue = e.getRequiredData(JiraDataKeys.ISSUE);
//
//        var taskManager = TaskManager.getManager(project);
//
//        var task = taskManager.findTask(issue.getKey());
//
//        if (task != null) {
//            taskManager.activateTask(task, false);
//        } else {
//            var issues = taskManager.getIssues(issue.getKey());
//
//            if (issues.isEmpty()) {
//                Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotification("No issue found", ""));
//            } else if (issues.size() == 1) {
//                showOpenTaskDialog(project, issues.get(0));
//            } else {
//                // when GotoTaskAction is performed, it takes initial search text from the already existing
//                // ChooseByNamePopup popup, if any, retrieved from the project by CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY key
//                // so, to filter tasks by issue key, we artificially create such popup and set predefinedText for it
//                // using default values (placeholders) for all other constructor arguments
//                var popup = new ChooseByNamePopup(
//                        project,
//                        new ListChooseByNameModel<>(project, "", "", List.of()), // placeholder
//                        new DefaultChooseByNameItemProvider(null), // placeholder
//                        null, // default
//                        issue.getKey(), // only this argument matters
//                        false, // default
//                        0 // default
//                ) {};
//
//                project.putUserData(ChooseByNamePopup.CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY, popup);
//
//                var action = ActionManager.getInstance().getAction(GotoTaskAction.ID);
//
//                action.actionPerformed(e); // GotoTaskAction retrieves only the project from AnActionEvent
//            }
//        }
    }
}
