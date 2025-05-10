package com.github.damiano1996.jetbrains.incoder.tool.window.agents.cards;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.*;

public interface Agent {

    String getDisplayName();

    String getDescription();

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
