package com.github.damiano1996.intellijplugin.incoder.llm;

import com.github.damiano1996.intellijplugin.incoder.llm.server.settings.ServerSettings;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class LlmFactoryProviderImpl implements LlmFactoryProvider {

    @Override
    public LlmAbstractFactory createLlmAbstractFactory(@NotNull ServerSettings settings) {
        switch (Objects.requireNonNull(settings.getState(), "LLM settings state must be defined.")
                .serverType) {
            case LOCAL -> {
                return new ContainerLlmAbstractFactoryImpl();
            }
            default -> throw new IllegalStateException(
                    "Unexpected value: " + settings.getState().serverType);
        }
    }
}
