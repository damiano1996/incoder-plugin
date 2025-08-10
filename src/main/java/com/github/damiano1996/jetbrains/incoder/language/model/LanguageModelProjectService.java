package com.github.damiano1996.jetbrains.incoder.language.model;

import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.ChatLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings.ChatSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.settings.InlineSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactoryUtils;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ThrowableComputable;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public final class LanguageModelProjectService implements LanguageModelService, Disposable {

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
    public ThrowableComputable<ChatLanguageModelClient, LanguageModelException> createChatClient(
            ChatSettings.State settings, LanguageModelParameters parameters) {
        return () -> {
            LanguageModelServer server =
                    ServerFactoryUtils.findByName(parameters.serverName).createServer();
            return server.createChatClient(parameters);
        };
    }

    @Override
    public ThrowableComputable<ChatLanguageModelClient, LanguageModelException>
            createChatClientWithDefaultSettings(LanguageModelParameters parameters) {
        return createChatClient(ChatSettings.getInstance().getState(), parameters);
    }

    @Override
    public ThrowableComputable<InlineLanguageModelClient, LanguageModelException>
            createInlineClient(InlineSettings.State settings, LanguageModelParameters parameters) {
        return () -> {
            LanguageModelServer server =
                    ServerFactoryUtils.findByName(parameters.serverName).createServer();
            return server.createInlineClient(parameters);
        };
    }

    @Override
    public ThrowableComputable<InlineLanguageModelClient, LanguageModelException>
            createInlineClientWithDefaultSettings(LanguageModelParameters parameters) {
        return createInlineClient(InlineSettings.getInstance().getState(), parameters);
    }

    @Override
    public void verify(LanguageModelParameters parameters) throws LanguageModelException {
        ServerFactoryUtils.findByName(parameters.serverName).createServer().verify(parameters);
    }

    @Override
    public void dispose() {}
}
