package com.github.damiano1996.intellijplugin.incoder.llm;

import com.github.damiano1996.intellijplugin.incoder.InCoderBundle;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionException;
import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletionListener;
import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableException;
import com.github.damiano1996.intellijplugin.incoder.llm.server.LlmServer;
import com.github.damiano1996.intellijplugin.incoder.llm.server.ServerException;
import com.github.damiano1996.intellijplugin.incoder.llm.server.settings.ServerSettings;
import com.github.damiano1996.intellijplugin.incoder.notification.NotificationService;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@Service(Service.Level.PROJECT)
public final class LlmService implements Disposable {

    private final Project project;

    private final List<CodeCompletionListener> listeners = new ArrayList<>();

    private final BlockingQueue<CodeCompletionContext> queue = new ArrayBlockingQueue<>(1);

    private final LlmServerFactory llmServerFactory = new LlmServerFactoryImpl();

    private LlmServer server;
    private LlmClient client;

    public LlmService(Project project) {
        this.project = project;
    }

    public static LlmService getInstance(@NotNull Project project) {
        return project.getService(LlmService.class);
    }

    public void init() {
        server = llmServerFactory.createServer(ServerSettings.getInstance());

        ProgressManager.getInstance()
                .run(
                        new Task.Backgroundable(
                                project, InCoderBundle.message("plugin-title"), false) {
                            public void run(@NotNull ProgressIndicator indicator) {
                                try {

                                    log.debug("Init server");
                                    server.subscribe(indicator::setText);
                                    server.init();
                                    log.debug("Server is ready");

                                    log.debug("Initializing the client");
                                    client = server.createClient();
                                    client.subscribe(indicator::setText);
                                    client.init();
                                    log.debug("Client initialized");

                                    NotificationService.getInstance(project)
                                            .notifyInfo("InCoder client is ready");
                                    log.debug("Starting a new thread to consume requests.");
                                    new Thread(new RequestRunnable()).start();

                                } catch (InitializableException | ServerException e) {
                                    log.error("Unable to initialize the client", e);
                                    NotificationService.getInstance(project)
                                            .notifyError(e.getMessage());
                                }
                            }
                        });
    }

    public void subscribe(CodeCompletionListener listener) {
        listeners.add(listener);
        log.debug("New listener added.");
    }

    private void notify(String codeCompletionPrediction) {
        listeners.forEach(
                listener -> listener.onCodeCompletionPrediction(codeCompletionPrediction));
    }

    private void notify(Throwable e) {
        listeners.forEach(listener -> listener.onCodeCompletionError(e));
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

    @Override
    public void dispose() {
        try {
            log.debug("Disposing LLM service...");
            client.close();
            log.debug("Client closed");
            server.close();
            log.debug("Server closed");
        } catch (InitializableException e) {
            log.error("Unable to dispose.", e);
        }
    }

    private class RequestRunnable implements Runnable {
        @Override
        public void run() {
            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    log.debug("Taking request");
                    CodeCompletionContext codeCompletionContext = queue.take();

                    try {
                        // TODO: decide where to strip and split
                        var prediction = client.codeComplete(codeCompletionContext);
                        log.debug("Prediction received from service: {}", prediction);

                        if (queue.isEmpty()) {
                            log.debug("Queue is empty, therefore this prediction is still useful");
                            LlmService.this.notify(prediction);
                        } else {
                            log.debug("Queue is not empty. Prediction is obsolete");
                        }

                    } catch (CodeCompletionException e) {
                        log.error("Error while executing the prediction.", e);
                        LlmService.this.notify(e);
                    }
                }
            } catch (InterruptedException e) {
                log.error("Error while processing queued request.", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
