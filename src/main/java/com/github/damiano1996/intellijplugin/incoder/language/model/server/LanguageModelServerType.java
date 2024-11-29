package com.github.damiano1996.intellijplugin.incoder.language.model.server;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.anthropic.AnthropicFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.ollama.OllamaFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.openai.OpenAiFactory;
import lombok.Getter;

@Getter
public enum LanguageModelServerType {
    OLLAMA("Ollama", new OllamaFactory()),
    OPENAI("Open AI", new OpenAiFactory()),
    ANTHROPIC("Anthropic", new AnthropicFactory());

    private final String displayName;
    private final ServerFactory serverFactory;

    LanguageModelServerType(String displayName, ServerFactory serverFactory) {
        this.displayName = displayName;
        this.serverFactory = serverFactory;
    }
}
