package com.github.damiano1996.intellijplugin.incoder.generation;

import lombok.NonNull;

public record CodeUpdateResponse(
        @NonNull String updatedCode,
        @NonNull String notes
) {
}
