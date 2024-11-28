package com.github.damiano1996.intellijplugin.incoder;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionService;
import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelService;
import com.github.damiano1996.intellijplugin.incoder.notification.NotificationService;
import com.github.damiano1996.intellijplugin.incoder.settings.PluginSettings;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import java.awt.*;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class InCoderActivity implements ProjectActivity {

    private static void configureBeforeInitServices(@NotNull Project project)
            throws LanguageModelException {
        if (PluginSettings.getInstance().getState().isPluginConfigured) {
            initServices(project);
        } else {
            NotificationService.getInstance(project).notifyFirstConfiguration();
        }
    }

    private static void notifyWelcomeMessage(@NotNull Project project) {
        if (PluginSettings.getInstance().getState().isFirstPluginRun) {
            NotificationService.getInstance(project).notifyWelcome();
            PluginSettings.getInstance().getState().isFirstPluginRun = false;
        }
    }

    public static void initServices(@NotNull Project project) throws LanguageModelException {
        log.debug("Initializing services...");
        LanguageModelService.getInstance(project).init();
        CodeCompletionService.getInstance(project).init();

        log.debug("Services initialized");
    }

    @Override
    public Object execute(
            @NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        log.debug("New project opened.");

        EventQueue.invokeLater(
                () -> {
                    try {
                        notifyWelcomeMessage(project);
                        configureBeforeInitServices(project);
                    } catch (Exception e) {
                        log.error("Error while initializing services", e);
                        NotificationService.getInstance(project).notifyError(e.getMessage());
                    }
                });
        return null;
    }
}
