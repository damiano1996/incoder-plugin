package com.github.damiano1996.jetbrains.incoder;

import com.github.damiano1996.jetbrains.incoder.notification.NotificationService;
import com.github.damiano1996.jetbrains.incoder.settings.PluginSettings;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
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

        return null;
    }
}
