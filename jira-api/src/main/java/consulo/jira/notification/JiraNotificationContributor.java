package consulo.jira.notification;

import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.ui.notification.NotificationDisplayType;
import consulo.project.ui.notification.NotificationGroup;
import consulo.project.ui.notification.NotificationGroupContributor;
import jakarta.annotation.Nonnull;

import java.util.function.Consumer;

/**
 * @author VISTALL
 * @since 2025-04-21
 */
@ExtensionImpl
public class JiraNotificationContributor implements NotificationGroupContributor {
    public static final NotificationGroup BALLON = NotificationGroup.balloonGroup("jiraBallon", LocalizeValue.localizeTODO("Jira Balloon Notifications"));

    public static final NotificationGroup STICKY = new NotificationGroup(
        "jiraSticky",
        LocalizeValue.localizeTODO("Jira Sticky Balloon Notification"),
        NotificationDisplayType.STICKY_BALLOON,
        true,
        null
    );

    @Override
    public void contribute(@Nonnull Consumer<NotificationGroup> consumer) {
        consumer.accept(BALLON);
        consumer.accept(STICKY);
    }
}
