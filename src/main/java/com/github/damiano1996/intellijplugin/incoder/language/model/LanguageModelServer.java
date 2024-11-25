package com.github.damiano1996.intellijplugin.incoder.language.model;

public interface LanguageModelServer {

    boolean isHealthy();

    LanguageModelClient createClient();
}
