package com.github.damiano1996.intellijplugin.incoder.language.model.server;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

public abstract class BaseServerConfigurable implements Configurable {

    @Override
    public final void apply() throws ConfigurationException {
        updateState();
        verifySettings();
    }

    /** Updates the server state based on the current configuration. */
    protected abstract void updateState();

    /**
     * Verifies the configured settings and throws a {@link ConfigurationException} if any issues
     * are found.
     *
     * @throws ConfigurationException if the settings are invalid.
     */
    protected abstract void verifySettings() throws ConfigurationException;
}
