package com.github.damiano1996.intellijplugin.incoder.generation;

import com.intellij.openapi.vfs.VirtualFile;
import lombok.NonNull;

public record CodeGenerationContext(
        @NonNull VirtualFile virtualFile,
        // @NonNull String filePath,
        @NonNull String prompt,
        @NonNull String actualCode) {}
