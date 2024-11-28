package com.github.damiano1996.intellijplugin.incoder.language.model.server;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.anthropic.AnthropicAbstractFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama.OllamaAbstractFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.openai.OpenAiAbstractFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.settings.ServerAbstractFactory;
import lombok.Getter;

@Getter
public enum ServerType {
    OLLAMA("Ollama", new OllamaAbstractFactory()),
    OPENAI("Open AI", new OpenAiAbstractFactory()),
    ANTHROPIC("Anthropic", new AnthropicAbstractFactory());

    private final String displayName;
    private final ServerAbstractFactory serverAbstractFactory;

    ServerType(String displayName, ServerAbstractFactory serverAbstractFactory) {
        this.displayName = displayName;
        this.serverAbstractFactory = serverAbstractFactory;
    }
}
