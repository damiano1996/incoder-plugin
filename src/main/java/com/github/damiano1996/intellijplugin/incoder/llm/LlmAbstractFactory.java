package com.github.damiano1996.intellijplugin.incoder.llm;

import com.github.damiano1996.intellijplugin.incoder.llm.client.LlmClient;
import com.github.damiano1996.intellijplugin.incoder.llm.server.LlmServer;

public interface LlmAbstractFactory {

    LlmClient createClient();

    LlmServer createServer();
}
