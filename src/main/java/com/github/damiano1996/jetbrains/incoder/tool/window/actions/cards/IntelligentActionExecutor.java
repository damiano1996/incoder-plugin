package com.github.damiano1996.jetbrains.incoder.tool.window.actions.cards;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.*;

/**
 * Represents an executor for intelligent actions. It provides methods to execute an action
 * asynchronously and cancel the execution if needed.
 */
public interface IntelligentActionExecutor {

    /**
     * Executes the intelligent action asynchronously.
     *
     * @param componentConsumer the consumer that accepts the generated Swing component
     * @param stopCondition the supplier that provides the condition for stopping the execution
     * @return a CompletableFuture representing the asynchronous execution
     */
    CompletableFuture<Void> execute(
            Consumer<JComponent> componentConsumer, Supplier<Boolean> stopCondition);
}
