package com.github.damiano1996.intellijplugin.incoder.notification;

import com.github.damiano1996.intellijplugin.incoder.InCoderBundle;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@Service(Service.Level.PROJECT)
public final class NotificationService {

    private final Project project;

    public NotificationService(Project project) {
        this.project = project;
    }

    public static NotificationService getInstance(@NotNull Project project) {
        return project.getService(NotificationService.class);
    }

    public void notifyInfo(String message) {
        log.debug("Notifying information: {}", message);
        notify(message, NotificationType.INFORMATION);
    }

    public void notifyWarning(String message) {
        log.debug("Notifying warning: {}", message);
        notify(message, NotificationType.WARNING);
    }

    public void notifyError(String message) {
        log.debug("Notifying error: {}", message);
        notify(message, NotificationType.ERROR);
    }

    private void notify(String message, NotificationType notificationType) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(InCoderBundle.message("notification-group"))
                .createNotification(
                        InCoderBundle.message("plugin-title"), message, notificationType)
                .notify(project);
    }

    public void notifyWelcome() {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(InCoderBundle.message("notification-group"))
                .createNotification(
                        InCoderBundle.message("plugin-title"),
                        InCoderBundle.message("notification-welcome"),
                        NotificationType.INFORMATION)
                .addAction(
                        NotificationAction.createSimple(
                                InCoderBundle.message("notification-settings-button"),
                                () -> {
                                    log.debug("Opening settings via notification.");
                                    ShowSettingsUtil.getInstance()
                                            .showSettingsDialog(
                                                    project, InCoderBundle.message("name"));
                                }))
                .notify(project);
    }
}
