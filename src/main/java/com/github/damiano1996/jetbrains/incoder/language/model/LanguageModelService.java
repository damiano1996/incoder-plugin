package com.github.damiano1996.jetbrains.incoder.language.model;

import com.github.damiano1996.jetbrains.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.jetbrains.incoder.language.model.client.LanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings.ChatSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.client.doc.settings.DocumentationSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.settings.InlineSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.client.prompt.PromptType;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactoryUtils;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerSettings;
import com.github.damiano1996.jetbrains.incoder.settings.PluginSettings;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import dev.langchain4j.service.TokenStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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

    public void init() throws LanguageModelException {
        init(
                ServerFactoryUtils.findByName(
                                ServerSettings.getInstance().getState().activeServerName)
                        .createServer());
    }

    public void init(LanguageModelServer server) throws LanguageModelException {
        log.debug("Initializing {}...", LanguageModelService.class.getSimpleName());

        this.server = server;

        client = this.server.createClient(project);
        log.debug("Client created successfully!");

        log.debug("Verifying server connection.");
        client.checkServerConnection();
        log.debug("Server connection verified.");

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
                        codeCompletionContext.leftContext(),
                        codeCompletionContext.rightContext());
    }

    public @NotNull CompletableFuture<PromptType> classify(String prompt) {
        return CompletableFuture.supplyAsync(
                () ->
                        Objects.requireNonNull(
                                        client,
                                        "Client must be initialized to classify the prompt.")
                                .classify(prompt));
    }

    public TokenStream streamChat(int memoryId, @NonNull Editor editor, @NonNull String prompt) {
        return Objects.requireNonNull(
                        client, "Client must be initialized to chat with the language model.")
                .streamChat(
                        memoryId,
                        ChatSettings.getInstance().getState().systemMessageInstructionsWithCode,
                        editor.getDocument().getText(),
                        editor.getVirtualFile().getPath(),
                        project.getBasePath(),
                        prompt);
    }

    public String chat(int memoryId, @NonNull Editor editor, @NonNull String prompt) {
        return Objects.requireNonNull(
                        client, "Client must be initialized to chat with the language model.")
                .chat(
                        memoryId,
                        ChatSettings.getInstance().getState().systemMessageInstructionsWithCode,
                        editor.getDocument().getText(),
                        editor.getVirtualFile().getPath(),
                        project.getBasePath(),
                        prompt);
    }

    public TokenStream streamChat(int memoryId, @NonNull String prompt) {
        return Objects.requireNonNull(
                        client, "Client must be initialized to chat with the language model.")
                .streamChat(
                        memoryId,
                        ChatSettings.getInstance().getState().systemMessageInstructions,
                        project.getBasePath(),
                        prompt);
    }

    public String chat(int memoryId, @NonNull String prompt) {
        return Objects.requireNonNull(
                        client, "Client must be initialized to chat with the language model.")
                .chat(
                        memoryId,
                        ChatSettings.getInstance().getState().systemMessageInstructions,
                        project.getBasePath(),
                        prompt);
    }

    public String createFileName(String fileContent, @NotNull String language) {
        return Objects.requireNonNull(client, "Client must be initialized to create file name.")
                .createFileName(fileContent, language);
    }

    public String document(String fileContent) {
        return Objects.requireNonNull(client, "Client must be initialized to document files.")
                .document(
                        DocumentationSettings.getInstance().getState().documentationInstructions,
                        fileContent);
    }

    @Override
    public void dispose() {}
}
