package com.github.damiano1996.intellijplugin.incoder.completion;

public interface CodeCompletion {

    String codeComplete(CodeCompletionContext context) throws CodeCompletionException;
}
