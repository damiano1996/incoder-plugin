package com.github.damiano1996.jetbrains.incoder.language.model.server.settings.ui;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import javax.swing.*;

public interface ProviderUIStrategy {
    String name();

    JPanel buildPanel();

    void applyDefaults(LanguageModelParameters defaults);

    LanguageModelParameters.LanguageModelParametersBuilder<?, ?> collect(
            LanguageModelParameters.LanguageModelParametersBuilder<?, ?> builder);

    String cardName();

    LanguageModelParameters.LanguageModelParametersBuilder<?, ?> getBuilder();
}
