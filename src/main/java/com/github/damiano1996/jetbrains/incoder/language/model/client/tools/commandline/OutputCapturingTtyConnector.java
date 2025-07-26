package com.github.damiano1996.jetbrains.incoder.language.model.client.tools.commandline;

import com.jediterm.core.util.TermSize;
import com.jediterm.terminal.TtyConnector;
import java.io.IOException;
import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class OutputCapturingTtyConnector implements TtyConnector {
    private final TtyConnector delegate;
    private final StringBuilder output;
    private final CompletableFuture<StringBuilder> commandExecutionOutputResult;
    private volatile boolean completed = false;
    private long lastActivityTime = System.currentTimeMillis();
    private static final long INACTIVITY_TIMEOUT_MS = 2000;
    private static final int MAX_OUTPUT_LINES = 150;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(
                    r -> {
                        Thread t = new Thread(r, "OutputCapture-InactivityChecker");
                        t.setDaemon(true);
                        return t;
                    });
    private volatile ScheduledFuture<?> inactivityCheckTask;

    public OutputCapturingTtyConnector(
            TtyConnector delegate, CompletableFuture<StringBuilder> commandExecutionOutputResult) {
        this.delegate = delegate;
        this.output = new StringBuilder();
        this.commandExecutionOutputResult = commandExecutionOutputResult;
    }

    @Override
    public int read(char[] buf, int offset, int length) throws IOException {
        int bytesRead = delegate.read(buf, offset, length);
        if (bytesRead > 0) {
            synchronized (output) {
                output.append(buf, offset, bytesRead);
                lastActivityTime = System.currentTimeMillis();
                log.debug("Output buffer size: {} chars", output.length());

                checkAndCompleteIfReady();
            }
        } else if (bytesRead == -1) {
            completeIfNotAlready();
        }
        return bytesRead;
    }

    private void checkAndCompleteIfReady() {
        if (completed) return;

        String currentOutput = output.toString();

        if (isCommandComplete(currentOutput)) {
            completeIfNotAlready();
            return;
        }

        long lineCount = currentOutput.chars().filter(ch -> ch == '\n').count();
        if (lineCount > MAX_OUTPUT_LINES) {
            log.info("Output exceeded {} lines, completing early", MAX_OUTPUT_LINES);
            completeIfNotAlready();
            return;
        }

        scheduleInactivityCheck();
    }

    private boolean isCommandComplete(String output) {
        String[] lines = output.split("\n");
        if (lines.length == 0) return false;

        String lastLine = lines[lines.length - 1].trim();

        return lastLine.matches(".*[$#>]\\s*$")
                || (!output.isEmpty()
                        && System.currentTimeMillis() - lastActivityTime > INACTIVITY_TIMEOUT_MS);
    }

    private void scheduleInactivityCheck() {
        if (inactivityCheckTask != null && !inactivityCheckTask.isDone()) {
            inactivityCheckTask.cancel(false);
        }

        inactivityCheckTask =
                scheduler.schedule(
                        () -> {
                            if (!completed
                                    && System.currentTimeMillis() - lastActivityTime
                                            > INACTIVITY_TIMEOUT_MS) {
                                log.info(
                                        "Command appears inactive for {}ms, completing",
                                        INACTIVITY_TIMEOUT_MS);
                                completeIfNotAlready();
                            }
                        },
                        INACTIVITY_TIMEOUT_MS + 100,
                        TimeUnit.MILLISECONDS);
    }

    private void completeIfNotAlready() {
        if (!completed) {
            completed = true;
            commandExecutionOutputResult.complete(output);
            log.info("Command output completed with {} characters", output.length());
        }
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        delegate.write(bytes);
    }

    @Override
    public void write(String string) throws IOException {
        delegate.write(string);
    }

    @Override
    public boolean isConnected() {
        return delegate.isConnected();
    }

    @Override
    public void close() {
        completeIfNotAlready();

        if (inactivityCheckTask != null && !inactivityCheckTask.isDone()) {
            inactivityCheckTask.cancel(false);
        }
        scheduler.shutdown();

        delegate.close();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public int waitFor() throws InterruptedException {
        return delegate.waitFor();
    }

    @Override
    public boolean ready() throws IOException {
        return delegate.ready();
    }

    @Override
    public void resize(@NotNull TermSize termSize) {
        delegate.resize(termSize);
    }
}
