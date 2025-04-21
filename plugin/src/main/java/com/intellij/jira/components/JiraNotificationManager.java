package com.intellij.jira.components;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.application.ApplicationManager;
import consulo.jira.notification.JiraNotificationContributor;
import consulo.project.ui.notification.Notification;
import consulo.project.ui.notification.NotificationType;
import jakarta.inject.Singleton;

@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
@Singleton
public class JiraNotificationManager {

    public static JiraNotificationManager getInstance() {
        return ApplicationManager.getApplication().getInstance(JiraNotificationManager.class);
    }

    public Notification createNotification(String title, String content) {
        return JiraNotificationContributor.BALLON.createNotification(title, content, NotificationType.INFORMATION, null);
    }

    public Notification createNotificationError(String title, String content) {
        return JiraNotificationContributor.STICKY.createNotification(title, content, NotificationType.ERROR, null);
    }

    public Notification createSilentNotification(String title, String content) {
        return JiraNotificationContributor.STICKY.createNotification(title, content, NotificationType.INFORMATION, null);
    }

}
