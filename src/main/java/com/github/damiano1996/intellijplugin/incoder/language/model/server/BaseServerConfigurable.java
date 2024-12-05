package com.github.damiano1996.intellijplugin.incoder.language.model.server;

import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelService;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import java.util.Objects;

public abstract class BaseServerConfigurable implements Configurable {

    @Override
    public final void apply() throws ConfigurationException {
        updateState();

        try {
            LanguageModelService.getInstance(Objects.requireNonNull(ProjectUtil.getActiveProject()))
                    .init();
        } catch (LanguageModelException e) {
            throw new ConfigurationException(
                    e.getMessage(), "Unable to Initialize the Language Model Service.");
        }
    }

    protected abstract void updateState() throws ConfigurationException;
}
