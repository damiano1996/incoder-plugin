package com.github.damiano1996.intellijplugin.incoder.completion;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelService;
import com.github.damiano1996.intellijplugin.incoder.language.model.client.inline.settings.InlineSettings;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class CodeCompletionQueue {

    private final Project project;
    private final CodeCompletionListener listener;

    private final BlockingQueue<CodeCompletionContext> queue = new ArrayBlockingQueue<>(1);

    public CodeCompletionQueue(Project project, CodeCompletionListener listener) {
        this.project = project;
        this.listener = listener;

        log.debug("Starting async code completion runnable");
        CompletableFuture.runAsync(new QueueConsumer());
    }

    public void add(CodeCompletionContext codeCompletionContext) {
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

    public class QueueConsumer implements Runnable {
        @Override
        public void run() {
            try {
                while (InlineSettings.getInstance().getState().enable) {

                    log.debug("Taking request");
                    CodeCompletionContext codeCompletionContext = queue.take();

                    try {
                        String completion =
                                LanguageModelService.getInstance(project).complete(codeCompletionContext)
                                        .split("\n")[0]
                                        .trim();

                        if (queue.isEmpty()) {
                            log.debug(
                                    "Queue is empty, therefore this prediction is still useful");

                            listener.onCodeCompletionPrediction(completion);
                        } else {
                            log.debug("Queue is not empty. Prediction is obsolete");
                        }

                    } catch (Exception e) {
                        log.warn("Something went wrong while completing code", e);
                        listener.onCodeCompletionError(e);
                    }
                }

                log.debug("Inline code completion loop ended.");

            } catch (InterruptedException e) {
                log.error("Error while processing queued request.", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
