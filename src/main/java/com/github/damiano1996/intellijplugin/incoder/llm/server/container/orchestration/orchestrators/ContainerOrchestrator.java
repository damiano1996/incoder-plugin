package com.github.damiano1996.intellijplugin.incoder.llm.server.container.orchestration.orchestrators;

import com.github.damiano1996.intellijplugin.incoder.llm.server.container.Container;
import com.github.damiano1996.intellijplugin.incoder.llm.server.container.ContainerException;
import com.github.damiano1996.intellijplugin.incoder.llm.server.container.ContainerState;
import org.jetbrains.annotations.NotNull;

public interface ContainerOrchestrator {

    void pull(String name, String version) throws ContainerException;

    Container start(@NotNull Container container) throws ContainerException;

    void restart(Container container) throws ContainerException;

    ContainerState getContainerState(@NotNull Container container) throws ContainerException;

    void stopContainer(@NotNull Container container) throws ContainerException;
}