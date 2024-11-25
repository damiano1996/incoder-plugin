package com.github.damiano1996.intellijplugin.incoder.language.model.servers;

import com.github.damiano1996.intellijplugin.incoder.language.model.servers.anthropic.AnthropicAbstractFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.servers.ollama.OllamaAbstractFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.servers.openai.OpenAiAbstractFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.settings.ServerAbstractFactory;
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
