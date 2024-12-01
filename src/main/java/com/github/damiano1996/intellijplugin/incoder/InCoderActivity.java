package com.github.damiano1996.intellijplugin.incoder;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionService;
import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelService;
import com.github.damiano1996.intellijplugin.incoder.notification.NotificationService;
import com.github.damiano1996.intellijplugin.incoder.settings.PluginSettings;
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

        EventQueue.invokeLater(
                () -> {
                    try {
                        if (PluginSettings.getInstance().getState().isPluginConfigured) {
                            log.debug("Initializing services...");
                            LanguageModelService.getInstance(project).init();
                            CodeCompletionService.getInstance(project).init();
                            log.debug("Services initialized.");
                        } else {
                            log.debug("Sending first config notification.");
                            NotificationService.getInstance(project)
                                    .notifyWithSettingsActionButton();
                        }
                    } catch (LanguageModelException e) {
                        log.error("Error while initializing services", e);
                        NotificationService.getInstance(project)
                                .notifyWithSettingsActionButton(
                                        e.getMessage(), NotificationType.ERROR);
                    }
                });
        return null;
    }
}
