package com.github.damiano1996.intellijplugin.incoder.llm.server;

import com.github.damiano1996.intellijplugin.incoder.initializable.Initializable;
import com.github.damiano1996.intellijplugin.incoder.llm.LlmClient;

public interface LlmServer extends Initializable {

    LlmClient createClient() throws ServerException;
}
