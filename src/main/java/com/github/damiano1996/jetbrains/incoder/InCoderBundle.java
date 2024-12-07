package com.github.damiano1996.jetbrains.incoder;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class InCoderBundle extends DynamicBundle {

    private static final String PATH_TO_BUNDLE = "messages.bundle";
    private static final InCoderBundle instance = new InCoderBundle();

    @Contract(pure = true)
    public static @Nls @NotNull String message(
            @NotNull @PropertyKey(resourceBundle = PATH_TO_BUNDLE) String key, Object... params) {
        return instance.getMessage(key, params);
    }

    private InCoderBundle() {
        super(PATH_TO_BUNDLE);
    }
}
