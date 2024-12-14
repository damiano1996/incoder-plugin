package com.github.damiano1996.jetbrains.incoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

@Slf4j
@UtilityClass
public class ClassInstantiator {

    /**
     * Finds and instantiates all subtypes of the specified class.
     *
     * @param clazz The base class to find implementations for.
     * @param <T> The type of the base class.
     * @return a list of instances of the found subclasses.
     */
    public <T> @NotNull List<T> findImplementations(@NotNull Class<T> clazz) {
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
