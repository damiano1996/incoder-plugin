package com.github.damiano1996.jetbrains.incoder.completion;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelServiceImpl;
import com.intellij.openapi.project.Project;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodeCompletionQueue implements Runnable {

    public static final String MARKDOWN_CODE_BLOCK_DELIMITER = "```";

    private final Project project;
    private final CodeCompletionListener listener;

    private final BlockingQueue<CodeCompletionContext> queue = new ArrayBlockingQueue<>(1);

    public CodeCompletionQueue(Project project, CodeCompletionListener listener) {
        this.project = project;
        this.listener = listener;
    }

    public void enqueue(CodeCompletionContext codeCompletionContext) {
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
    public void run() {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {

                log.debug("Taking request");
                CodeCompletionContext codeCompletionContext = queue.take();

                log.debug("Consuming code completion request");

                try {
                    String completion =
                            LanguageModelServiceImpl.getInstance(project)
                                    .getClient()
                                    .complete(codeCompletionContext)
                                    .split("\n")[0]
                                    .trim();

                    log.debug(
                            "{}\nContinues with:\n{}",
                            codeCompletionContext.leftContext(),
                            completion);

                    if (completion.startsWith(MARKDOWN_CODE_BLOCK_DELIMITER)) continue;

                    if (queue.isEmpty()) {
                        log.debug("Queue is empty, therefore this prediction is still useful");

                        listener.onCodeCompletionPrediction(completion);
                    } else {
                        log.debug("Queue is not empty. Prediction is obsolete");
                    }

                } catch (Exception e) {
                    log.warn("Something went wrong while completing code", e);
                    listener.onCodeCompletionError(e);
                }
            }

        } catch (InterruptedException e) {
            log.error("Error while processing queued request.", e);
            Thread.currentThread().interrupt();
        }
    }
}
