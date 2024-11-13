package com.github.damiano1996.intellijplugin.incoder.generation;

public interface CodeGeneration {

    CodeUpdateResponse generate(CodeGenerationContext codeGenerationContext) throws CodeGenerationException;
}
