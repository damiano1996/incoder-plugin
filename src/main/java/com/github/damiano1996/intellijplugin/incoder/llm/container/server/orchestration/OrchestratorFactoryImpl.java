package com.github.damiano1996.intellijplugin.incoder.llm.container.server.orchestration;

import com.github.damiano1996.intellijplugin.incoder.llm.container.server.orchestration.orchestrators.ContainerOrchestrator;
import com.github.damiano1996.intellijplugin.incoder.llm.container.server.orchestration.orchestrators.OrchestratorType;
import com.github.damiano1996.intellijplugin.incoder.llm.container.server.orchestration.orchestrators.docker.DockerContainerOrchestrator;
import org.jetbrains.annotations.NotNull;

public class OrchestratorFactoryImpl implements OrchestratorFactory {
    @Override
    public ContainerOrchestrator create(@NotNull OrchestratorType orchestratorType) {
        switch (orchestratorType) {
            case DOCKER -> {
                return new DockerContainerOrchestrator();
            }
            default -> throw new IllegalStateException("Unexpected value: " + orchestratorType);
        }
    }
}
