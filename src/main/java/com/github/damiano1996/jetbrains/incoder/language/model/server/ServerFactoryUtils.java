package com.github.damiano1996.jetbrains.incoder.language.model.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

@Slf4j
@UtilityClass
public class ServerFactoryUtils {

    public ServerFactory findByName(@NotNull String name) throws LanguageModelException {
        if (name.isBlank() || name.isEmpty()) throw new LanguageModelException("Server name must be defined.");

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
        return findImplementations(ServerFactory.class);
    }

    /**
     * Finds and instantiates all subtypes of the specified class.
     *
     * @param clazz The base class to find implementations for.
     * @param <T> The type of the base class.
     * @return a list of instances of the found subclasses.
     */
    private <T> @NotNull List<T> findImplementations(@NotNull Class<T> clazz) {
        List<T> implementations = new ArrayList<>();

        try {
            String packageName = clazz.getPackageName();
            Reflections reflections = new Reflections(packageName);

            Set<Class<? extends T>> subTypes = reflections.getSubTypesOf(clazz);

            for (Class<? extends T> subtype : subTypes) {
                log.debug("Found implementation: {}", subtype.getName());
                try {
                    T instance = subtype.getDeclaredConstructor().newInstance();
                    implementations.add(instance);
                    log.debug(
                            "Instance {} added successfully", instance.getClass().getSimpleName());
                } catch (ReflectiveOperationException e) {
                    log.error("Failed to instantiate implementation: {}", subtype.getName(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error finding implementations of {}", clazz.getName(), e);
        }

        return implementations;
    }
}
