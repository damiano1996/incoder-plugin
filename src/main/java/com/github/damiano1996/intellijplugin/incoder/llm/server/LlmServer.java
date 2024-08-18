package com.github.damiano1996.intellijplugin.incoder.llm.server;

import com.github.damiano1996.intellijplugin.incoder.initializable.Initializable;
import java.net.URL;

public interface LlmServer extends Initializable {

    URL getBaseUrl() throws ServerException;
}
