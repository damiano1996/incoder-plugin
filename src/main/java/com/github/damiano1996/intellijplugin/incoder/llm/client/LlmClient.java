package com.github.damiano1996.intellijplugin.incoder.llm.client;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletion;
import com.github.damiano1996.intellijplugin.incoder.initializable.Initializable;
import java.net.URL;

public interface LlmClient extends Initializable, CodeCompletion {

    void updateBaseUrl(URL baseUrl);
}
