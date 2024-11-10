package com.github.damiano1996.intellijplugin.incoder.generation;

public interface CodeGeneration {

    String generate(CodeGenerationContext codeGenerationContext) throws CodeGenerationException;
}
