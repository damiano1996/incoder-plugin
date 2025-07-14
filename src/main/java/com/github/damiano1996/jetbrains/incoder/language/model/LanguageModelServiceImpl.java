package com.github.damiano1996.jetbrains.incoder.language.model;

import com.github.damiano1996.jetbrains.incoder.language.model.client.LanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactoryUtils;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerSettings;
import com.github.damiano1996.jetbrains.incoder.settings.PluginSettings;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
@Service(Service.Level.PROJECT)
public final class LanguageModelServiceImpl implements LanguageModelService, Disposable {

    private final Project project;

    @Nullable private LanguageModelServer server;
    @Nullable private LanguageModelClient client;

    public LanguageModelServiceImpl(Project project) {
        this.project = project;
    }

    public static LanguageModelServiceImpl getInstance(@NotNull Project project) {
        return project.getService(LanguageModelServiceImpl.class);
    }

    @Override
    public void init() throws LanguageModelException {
        init(
                ServerFactoryUtils.findByName(
                                ServerSettings.getInstance().getState().activeServerName)
                        .createServer());
    }

    @Override
    public void init(LanguageModelServer server) throws LanguageModelException {
        log.debug("Initializing {}...", LanguageModelServiceImpl.class.getSimpleName());

        this.server = server;

        client = this.server.createClient(project);
        log.debug("Client created successfully!");

        log.debug("Verifying server connection.");
        client.checkServerConnection();
        log.debug("Server connection verified.");

        PluginSettings.getInstance().getState().isPluginConfigured = true;
        log.debug("Client and server started. Plugin can be considered configured.");
    }

    @Override
    public boolean isReady() {
        return server != null && client != null;
    }

    @Override
    public String getSelectedModelName() {
        if (server == null) throw new IllegalStateException("Server must be initialized.");
        return server.getSelectedModelName();
    }

    @Override
    public @NotNull LanguageModelClient getClient() {
        if (client == null) throw new IllegalStateException("Client must be initialized.");
        return client;
    }

    @Override
    public void dispose() {}
}
