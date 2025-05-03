package com.intellij.jira.tasks;

import com.intellij.jira.components.JiraNotificationManager;
import consulo.application.progress.ProgressIndicator;
import consulo.application.progress.Task;
import consulo.project.Project;
import consulo.project.ui.notification.Notifications;
import consulo.versionControlSystem.change.ChangeListManager;
import consulo.versionControlSystem.change.LocalChangeList;
import jakarta.annotation.Nonnull;

public class AddChangelistTask extends Task.Backgroundable {

    private final String changelistName;
    private final boolean isDefault;

    public AddChangelistTask(@Nonnull Project project, @Nonnull String changelistName, boolean isDefault) {
        super(project, "Creating changelist...");
        this.changelistName = changelistName;
        this.isDefault = isDefault;
    }

    @Override
    public void run(@Nonnull ProgressIndicator indicator) {
        ChangeListManager changeListManager = ChangeListManager.getInstance((Project) myProject);
        LocalChangeList localChangeList = changeListManager.addChangeList(changelistName, null);
        if (isDefault) {
            changeListManager.setDefaultChangeList(localChangeList);
        }
    }

    @Override
    public void onSuccess() {
        Notifications.Bus.notify(JiraNotificationManager.getInstance().createSilentNotification("Created '" + changelistName + "' Changelist", ""));
    }

}
