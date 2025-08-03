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
                                var server =
                                        ServerFactoryUtils.findByName(serverName).createServer();
                                return server.createClient();
                        },
                        "Starting the Language Model Service for %s".formatted(serverName),
                        false,
                        project);
    }

    @Override
    public void dispose() {}
}
