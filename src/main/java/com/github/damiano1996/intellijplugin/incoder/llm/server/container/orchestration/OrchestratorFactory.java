package com.github.damiano1996.intellijplugin.incoder.llm.server.container.orchestration;

import com.github.damiano1996.intellijplugin.incoder.llm.server.container.orchestration.orchestrators.ContainerOrchestrator;
import com.github.damiano1996.intellijplugin.incoder.llm.server.container.orchestration.orchestrators.OrchestratorType;

public interface OrchestratorFactory {

    ContainerOrchestrator create(OrchestratorType orchestratorType);
}
