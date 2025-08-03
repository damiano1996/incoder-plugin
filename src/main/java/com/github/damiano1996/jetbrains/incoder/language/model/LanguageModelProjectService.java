package com.github.damiano1996.jetbrains.incoder.language.model;

import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.ChatLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings.ChatSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.settings.InlineSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactoryUtils;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.Project;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public final class LanguageModelProjectService implements LanguageModelService, Disposable {

    private final Project project;

    @Nullable private ChatLanguageModelClient chatLanguageModelClient;
    @Nullable private InlineLanguageModelClient inlineLanguageModelClient;

    public LanguageModelProjectService(Project project) {
        this.project = project;
    }

    public static LanguageModelService getInstance(@NotNull Project project) {
        return project.getService(LanguageModelService.class);
    }

    @Override
    public Set<String> getAvailableServerNames() {
        return ServerFactoryUtils.getServerFactories().stream()
                .map(serverFactory -> serverFactory.createServer().getName())
                .collect(Collectors.toSet());
    }

    @Override
    public LanguageModelService with(InlineSettings.State settings) throws LanguageModelException {
        //noinspection DialogTitleCapitalization
        inlineLanguageModelClient =
                ProgressManager.getInstance()
                        .runProcessWithProgressSynchronously(
                                () -> {
                                    LanguageModelServer server =
                                            ServerFactoryUtils.findByName(settings.serverName)
                                                    .createServer();
                                    return server.createInlineClient();
                                },
                                "Creating the inline client for %s".formatted(settings),
                                false,
                                project);
        return this;
    }

    @Override
    public LanguageModelService with(ChatSettings.State settings) throws LanguageModelException {
        //noinspection DialogTitleCapitalization
        chatLanguageModelClient =
                ProgressManager.getInstance()
                        .runProcessWithProgressSynchronously(
                                () -> {
                                    LanguageModelServer server =
                                            ServerFactoryUtils.findByName(settings.serverName)
                                                    .createServer();
                                    return server.createChatClient();
                                },
                                "Creating the chat client for %s".formatted(settings),
                                false,
                                project);
        return this;
    }

    @Override
    public ChatLanguageModelClient getOrCreateChatClient() throws LanguageModelException {
        if (chatLanguageModelClient == null) with(ChatSettings.getInstance().getState());
        return chatLanguageModelClient;
    }

    @Override
    public InlineLanguageModelClient getOrCreateInlineClient() throws LanguageModelException {
        if (inlineLanguageModelClient == null) with(InlineSettings.getInstance().getState());
        return inlineLanguageModelClient;
    }

    @Override
    public void dispose() {}
}
