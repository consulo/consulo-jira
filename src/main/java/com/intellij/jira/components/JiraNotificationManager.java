package com.intellij.jira.components;

import consulo.application.ApplicationManager;
import consulo.project.ui.notification.Notification;
import consulo.project.ui.notification.NotificationGroup;
import consulo.project.ui.notification.NotificationType;

public class JiraNotificationManager {

    private static final String BALLON_NOTIFICATION_GROUP_NAME = "Jira Balloon Notifications";
    private static final String STICKY_BALLON_NOTIFICATION_GROUP_NAME = "Jira Sticky Balloon Notifications";

    public static JiraNotificationManager getInstance(){
        return ApplicationManager.getApplication().getInstance(JiraNotificationManager.class);
    }

    public Notification createNotification(String title, String content){
        return getNotificationGroup(BALLON_NOTIFICATION_GROUP_NAME).createNotification(title, content, NotificationType.INFORMATION);
    }

    public Notification createNotificationError(String title, String content){
        return getNotificationGroup(STICKY_BALLON_NOTIFICATION_GROUP_NAME).createNotification(title, content, NotificationType.ERROR);
    }

    public Notification createSilentNotification(String title, String content){
        return getNotificationGroup(BALLON_NOTIFICATION_GROUP_NAME).createNotification(title, content, NotificationType.INFORMATION);
    }

    private static NotificationGroup getNotificationGroup(String name) {
        return NotificationGroupManager.getInstance().getNotificationGroup(name);
    }

}
