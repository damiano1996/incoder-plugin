package com.github.damiano1996.intellijplugin.incoder.language.model;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionListener;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.LanguageModelClient;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.chat.settings.ChatSettings;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.prompt.PromptType;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.LanguageModelServer;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.settings.ServerSettings;
import com.github.damiano1996.intellijplugin.incoder.notification.NotificationService;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import dev.langchain4j.service.TokenStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Slf4j
@Service(Service.Level.PROJECT)
public final class LanguageModelService {

    private final Project project;

    private final List<CodeCompletionListener> listeners = new ArrayList<>();

    private final BlockingQueue<CodeCompletionContext> queue = new ArrayBlockingQueue<>(1);

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
                ServerSettings.getInstance()
                        .getState()
                        .modelType
                        .getServerFactory()
                        .createServer();

        log.debug("Initializing the client");
        client = server.createClient();

        log.debug("Client initialized");

        NotificationService.getInstance(project).notifyInfo("InCoder client is ready");
        log.debug("Starting a new thread to consume requests.");
        new Thread(new RequestRunnable()).start();
    }

    public void subscribe(CodeCompletionListener listener) {
        listeners.add(listener);
        log.debug("New listener added.");
    }

    private void notify(String codeCompletionPrediction) {
        listeners.forEach(
                listener -> listener.onCodeCompletionPrediction(codeCompletionPrediction));
    }

    public void autocompletion(CodeCompletionContext codeCompletionContext) {
        log.debug("Adding new code completion context to queue");
        try {
            if (!queue.offer(codeCompletionContext)) {
                queue.poll();
                queue.put(codeCompletionContext);
            }
        } catch (InterruptedException e) {
            log.warn("Unable to put element in queue. {}", e.getMessage());
        }
    }

    public String getSelectedModelName() {
        return server.getSelectedModelName();
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
        return client.chat(ChatSettings.getInstance().getState().systemMessageInstructions, project.getBasePath(), editDescription);
    }

    public String createFileName(String fileContent) {
        return client.createFileName(fileContent);
    }

    private class RequestRunnable implements Runnable {
        @Override
        public void run() {
            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    log.debug("Taking request");
                    CodeCompletionContext codeCompletionContext = queue.take();

                    // todo: improve it exploiting streaming
                    //                    client.complete(
                    //                                    codeCompletionContext.leftContext(),
                    //                                    codeCompletionContext.rightContext())
                    //                            .onNext(s -> {})
                    //                            .onComplete(
                    //                                    aiMessageResponse -> {
                    //                                        String completion =
                    //                                                aiMessageResponse
                    //                                                        .content()
                    //                                                        .text()
                    //                                                        .split("\n")[0]
                    //                                                        .trim();
                    //
                    //                                        if (queue.isEmpty()) {
                    //                                            log.debug(
                    //                                                    "Queue is empty, therefore
                    // this prediction is"
                    //                                                            + " still
                    // useful");
                    //
                    // LanguageModelService.this.notify(completion);
                    //                                        } else {
                    //                                            log.debug("Queue is not empty.
                    // Prediction is obsolete");
                    //                                        }
                    //                                    })
                    //                            .onError(throwable -> {})
                    //                            .start();
                }
            } catch (InterruptedException e) {
                log.error("Error while processing queued request.", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
