package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.github.damiano1996.jetbrains.incoder.ClassInstantiator;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@UtilityClass
public class ServerFactoryUtils {

    public ServerFactory findByName(@NotNull String name) throws LanguageModelException {
        if (name.isBlank()) throw new LanguageModelException("Server name must be defined.");

        return getServerFactories().stream()
                .filter(serverFactory -> serverFactory.getName().equals(name))
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "No server factory implementation found for %s"
                                                .formatted(name)));
    }

    /**
     * Retrieves all implementations of the {@link ServerFactory} interface.
     *
     * @return a list of server factory instances.
     */
    public @NotNull List<ServerFactory> getServerFactories() {
        return ClassInstantiator.findImplementations(ServerFactory.class);
    }

    public List<String> getServerNames() {
        return getServerFactories().stream()
                .map(
                        serverFactory -> {
                            try {
                                return serverFactory.createServer().getName();
                            } catch (LanguageModelException e) {
                                log.warn("Unable to create server", e);
                                return null;
                            }
                        })
                .toList();
    }
}
