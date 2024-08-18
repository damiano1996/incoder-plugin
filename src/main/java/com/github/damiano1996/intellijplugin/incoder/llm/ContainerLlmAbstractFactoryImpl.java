package com.github.damiano1996.intellijplugin.incoder.llm;

import com.github.damiano1996.intellijplugin.incoder.llm.client.LlmClient;
import com.github.damiano1996.intellijplugin.incoder.llm.client.container.ContainerLlmClient;
import com.github.damiano1996.intellijplugin.incoder.llm.server.LlmServer;
import com.github.damiano1996.intellijplugin.incoder.llm.server.container.ContainerLlmServer;

public class ContainerLlmAbstractFactoryImpl implements LlmAbstractFactory {
    @Override
    public LlmClient createClient() {
        return new ContainerLlmClient();
    }

    @Override
    public LlmServer createServer() {
        return new ContainerLlmServer();
    }
}
