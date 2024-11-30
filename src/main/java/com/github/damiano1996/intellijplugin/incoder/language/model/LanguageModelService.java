package com.github.damiano1996.intellijplugin.incoder.language.model;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.LanguageModelClient;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.chat.settings.ChatSettings;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.inline.settings.InlineSettings;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.prompt.PromptType;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.settings.ServerSettings;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import dev.langchain4j.service.TokenStream;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Slf4j
@Service(Service.Level.PROJECT)
public final class LanguageModelService {

    private final Project project;

    private LanguageModelServer server;
    private LanguageModelClient client;

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
        server =
                ServerSettings.getInstance().getState().modelType.getServerFactory().createServer();

        client = server.createClient();

        log.debug("Server and clients are ready");
    }

    public String getSelectedModelName() {
        return server.getSelectedModelName();
    }

    public String complete(@NotNull CodeCompletionContext codeCompletionContext) {
        return client.complete(
                InlineSettings.getInstance().getState().systemMessageInstructions,
                codeCompletionContext.leftContext());
    }

    @Contract("_ -> new")
    public @NotNull CompletableFuture<PromptType> classify(String prompt) {
        return CompletableFuture.supplyAsync(() -> client.classify(prompt));
    }

    public TokenStream chat(@NonNull Editor editor, @NonNull String editDescription) {
        return client.chat(
                ChatSettings.getInstance().getState().systemMessageInstructionsWithCode,
                editor.getDocument().getText(),
                editor.getVirtualFile().getPath(),
                project.getBasePath(),
                editDescription);
    }

    public TokenStream chat(@NonNull String editDescription) {
        return client.chat(
                ChatSettings.getInstance().getState().systemMessageInstructions,
                project.getBasePath(),
                editDescription);
    }

    public String createFileName(String fileContent) {
        return client.createFileName(fileContent);
    }
}
