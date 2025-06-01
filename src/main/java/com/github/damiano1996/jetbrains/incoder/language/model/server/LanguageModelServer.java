package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.client.LanguageModelClient;
import java.util.List;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface LanguageModelServer {

    String getName();

    List<String> getAvailableModels();

    String getSelectedModelName();

    @NotNull
    LanguageModelClient createClient(Project project) throws LanguageModelException;
}
