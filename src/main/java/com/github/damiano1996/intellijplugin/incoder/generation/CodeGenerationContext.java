package com.github.damiano1996.intellijplugin.incoder.generation;

import lombok.NonNull;

public record CodeGenerationContext(
        @NonNull String prompt,
        @NonNull String actualCode
) {
}
