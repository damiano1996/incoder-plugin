package com.github.damiano1996.intellijplugin.incoder.language.model;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.LanguageModelClient;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.chat.settings.ChatSettings;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.inline.settings.InlineSettings;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.prompt.PromptType;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.ServerFactoryUtils;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.settings.ServerSettings;
import com.github.damiano1996.intellijplugin.incoder.settings.PluginSettings;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import dev.langchain4j.service.TokenStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
@Service(Service.Level.PROJECT)
public final class LanguageModelService implements Disposable {

    private final Project project;

    @Nullable private LanguageModelServer server;
    @Nullable private LanguageModelClient client;

    public LanguageModelService(Project project) {
        this.project = project;
    }

    public static LanguageModelService getInstance(@NotNull Project project) {
        return project.getService(LanguageModelService.class);
    }

    public static LanguageModelService getInstance() {
        return LanguageModelService.getInstance(ProjectManager.getInstance().getDefaultProject());
    }

    public void init() throws LanguageModelException {
        log.debug("Initializing {}...", LanguageModelService.class.getSimpleName());

        server =
                ServerFactoryUtils.findByName(ServerSettings.getInstance().getState().serverName)
                        .createServer();

        client = server.createClient();
        log.debug("Client created successfully!");

        PluginSettings.getInstance().getState().isPluginConfigured = true;
        log.debug("Client and server started. Plugin can be considered configured.");
    }

    public boolean isReady() {
        return server != null && client != null;
    }

    public String getSelectedModelName() {
        return Objects.requireNonNull(
                        server, "Server must be initialized to retrieve the selected model name.")
                .getSelectedModelName();
    }

    public String complete(@NotNull CodeCompletionContext codeCompletionContext) {
        return Objects.requireNonNull(client, "Client must be initialized to complete the code.")
                .complete(
                        InlineSettings.getInstance().getState().systemMessageInstructions,
                        codeCompletionContext.leftContext());
    }

    @Contract("_ -> new")
    public @NotNull CompletableFuture<PromptType> classify(String prompt) {
        return CompletableFuture.supplyAsync(
                () ->
                        Objects.requireNonNull(
                                        client,
                                        "Client must be initialized to classify the prompt.")
                                .classify(prompt));
    }

    public TokenStream chat(@NonNull Editor editor, @NonNull String editDescription) {
        return Objects.requireNonNull(
                        client, "Client must be initialized to chat with the language model.")
                .chat(
                        ChatSettings.getInstance().getState().systemMessageInstructionsWithCode,
                        editor.getDocument().getText(),
                        editor.getVirtualFile().getPath(),
                        project.getBasePath(),
                        editDescription);
    }

    public TokenStream chat(@NonNull String editDescription) {
        return Objects.requireNonNull(
                        client, "Client must be initialized to chat with the language model.")
                .chat(
                        ChatSettings.getInstance().getState().systemMessageInstructions,
                        project.getBasePath(),
                        editDescription);
    }

    public String createFileName(String fileContent) throws LanguageModelException {
        return Objects.requireNonNull(client, "Client must be initialized to create file name.")
                .createFileName(fileContent);
    }

    @Override
    public void dispose() {}
}
