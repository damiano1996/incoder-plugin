package com.github.damiano1996.jetbrains.incoder.language.model.server;

import javax.swing.*;

public interface ProviderUIStrategy {
    String name();

    JPanel buildPanel();

    void applyDefaults(LanguageModelParameters defaults);

    LanguageModelParameters collect(LanguageModelParameters common);

    String cardName();
}
