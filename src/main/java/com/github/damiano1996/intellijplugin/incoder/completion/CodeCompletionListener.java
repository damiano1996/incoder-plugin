package com.github.damiano1996.intellijplugin.incoder.completion;

public interface CodeCompletionListener {

    void onCodeCompletionPrediction(String prediction);

    void onCodeCompletionError(Throwable throwable);
}
