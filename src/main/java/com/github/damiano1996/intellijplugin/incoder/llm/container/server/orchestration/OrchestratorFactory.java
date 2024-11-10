package com.github.damiano1996.intellijplugin.incoder.llm.container.server.orchestration;

import com.github.damiano1996.intellijplugin.incoder.llm.container.server.orchestration.orchestrators.ContainerOrchestrator;
import com.github.damiano1996.intellijplugin.incoder.llm.container.server.orchestration.orchestrators.OrchestratorType;

public interface OrchestratorFactory {

    ContainerOrchestrator create(OrchestratorType orchestratorType);
}