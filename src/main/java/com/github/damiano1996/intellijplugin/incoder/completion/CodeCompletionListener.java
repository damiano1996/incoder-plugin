package com.github.damiano1996.intellijplugin.incoder.completion;

/** Interface for handling code completion events in the InCoder plugin. */
public interface CodeCompletionListener {

    /**
     * Called when a code completion prediction is available.
     *
     * @param prediction The predicted code snippet or suggestion.
     */
    void onCodeCompletionPrediction(String prediction);

    /**
     * Called when an error occurs during code completion processing.
     *
     * @param throwable The exception that caused the error.
     */
    void onCodeCompletionError(Throwable throwable);
}
