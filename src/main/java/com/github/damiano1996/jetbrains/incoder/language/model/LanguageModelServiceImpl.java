package com.github.damiano1996.jetbrains.incoder.language.model;

import com.github.damiano1996.jetbrains.incoder.language.model.client.LanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactoryUtils;
import com.github.damiano1996.jetbrains.incoder.notification.NotificationService;
import com.github.damiano1996.jetbrains.incoder.settings.PluginSettings;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public final class LanguageModelServiceImpl implements LanguageModelService, Disposable {

    private final Project project;

    public LanguageModelServiceImpl(Project project) {
        this.project = project;
    }

    public static LanguageModelService getInstance(@NotNull Project project) {
        return project.getService(LanguageModelService.class);
    }

    @Override
    public LanguageModelClient createClient(String serverName) throws LanguageModelException {
        //noinspection DialogTitleCapitalization
        return ProgressManager.getInstance()
                .runProcessWithProgressSynchronously(
                        () -> {
                            try {
                                var server =
                                        ServerFactoryUtils.findByName(serverName).createServer();

                                PluginSettings.getInstance().getState().isPluginConfigured = true;

                                return server.createClient();
                            } catch (LanguageModelException e) {
                                notifyInitializationError(e);
                                throw e;
                            }
                        },
                        "Starting the Language Model Service for %s".formatted(serverName),
                        false,
                        project);
    }

    private void notifyInitializationError(LanguageModelException e) {
        if (PluginSettings.getInstance().getState().isPluginConfigured) {
            log.debug("Plugin is configured, notifying with error.");
            NotificationService.getInstance(project)
                    .notifyWithSettingsActionButton(e.getMessage(), NotificationType.ERROR);
        } else {
            log.debug(
                    "Plugin is not configured. " + "Showing default message with settings button.");
            NotificationService.getInstance(project).notifyWithSettingsActionButton();
        }
    }

    @Override
    public void dispose() {}
}
