package com.github.damiano1996.intellijplugin.incoder.llm;

import com.github.damiano1996.intellijplugin.incoder.llm.container.server.ContainerLlmServer;
import com.github.damiano1996.intellijplugin.incoder.llm.langchain.server.LangChainLlmServer;
import com.github.damiano1996.intellijplugin.incoder.llm.server.LlmServer;
import com.github.damiano1996.intellijplugin.incoder.llm.server.settings.ServerSettings;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class LlmServerFactoryImpl implements LlmServerFactory {

    @Override
    public LlmServer createServer(@NotNull ServerSettings settings) {
        switch (Objects.requireNonNull(settings.getState(), "LLM settings state must be defined.")
                .serverType) {
            case LOCAL_CONTAINER -> {
                return new ContainerLlmServer();
            }
            case LANG_CHAIN_4J -> {
                return new LangChainLlmServer();
            }
            default -> throw new IllegalStateException(
                    "Unexpected value: " + settings.getState().serverType);
        }
    }
}
