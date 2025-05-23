package com.github.damiano1996.jetbrains.incoder;

import com.github.damiano1996.jetbrains.incoder.completion.CodeCompletionService;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelService;
import com.github.damiano1996.jetbrains.incoder.notification.NotificationService;
import com.github.damiano1996.jetbrains.incoder.settings.PluginSettings;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import java.awt.*;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class InCoderActivity implements ProjectActivity {

    @Override
    public Object execute(
            @NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        log.debug("New project opened.");

        if (PluginSettings.getInstance().getState().isFirstPluginRun) {
            NotificationService.getInstance(project).notifyWelcome();
            PluginSettings.getInstance().getState().isFirstPluginRun = false;
        }

        initServices(project);
        return null;
    }

    private static void initServices(@NotNull Project project) {
        EventQueue.invokeLater(
                () -> {
                    try {
                        log.debug("Initializing services...");
                        LanguageModelService.getInstance(project).init();
                        CodeCompletionService.getInstance(project).init();
                        log.debug("Services initialized.");
                    } catch (LanguageModelException e) {
                        log.warn("Unable to init services.", e);

                        if (PluginSettings.getInstance().getState().isPluginConfigured) {
                            log.debug("Plugin is configured, notifying with error.");
                            NotificationService.getInstance(project)
                                    .notifyWithSettingsActionButton(
                                            e.getMessage(), NotificationType.ERROR);
                        } else {
                            log.debug(
                                    "Plugin is not configured. "
                                            + "Showing default message with settings button.");
                            NotificationService.getInstance(project)
                                    .notifyWithSettingsActionButton();
                        }
                    }
                });
    }
}
