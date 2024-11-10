package com.github.damiano1996.intellijplugin.incoder.llm;

import com.github.damiano1996.intellijplugin.incoder.completion.CodeCompletion;
import com.github.damiano1996.intellijplugin.incoder.generation.CodeGeneration;
import com.github.damiano1996.intellijplugin.incoder.initializable.Initializable;

public interface LlmClient extends Initializable, CodeCompletion, CodeGeneration {}
