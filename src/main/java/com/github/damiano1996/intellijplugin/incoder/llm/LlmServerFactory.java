package com.github.damiano1996.intellijplugin.incoder.llm;

import com.github.damiano1996.intellijplugin.incoder.llm.server.LlmServer;
import com.github.damiano1996.intellijplugin.incoder.llm.server.settings.ServerSettings;

public interface LlmServerFactory {

    LlmServer createServer(ServerSettings settings);
}
