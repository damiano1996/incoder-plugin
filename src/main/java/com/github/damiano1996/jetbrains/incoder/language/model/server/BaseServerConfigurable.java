package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.ThrowableComputable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseServerConfigurable implements Configurable {

    protected abstract ServerFactory getServerFactory();

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return getServerFactory().getName();
    }

    @Override
    public final void apply() throws ConfigurationException {
        //noinspection DialogTitleCapitalization
        ProgressManager.getInstance()
                .runProcessWithProgressSynchronously(
                        (ThrowableComputable<Void, ConfigurationException>)
                                () -> {
                                    updateState();
                                    verifySettings();
                                    return null;
                                },
                        "Verifying %s Settings".formatted(getDisplayName()),
                        false,
                        null);
    }

    /** Updates the server state based on the current configuration. */
    protected abstract void updateState();

    /**
     * Verifies the configured settings and throws a {@link ConfigurationException} if any issues
     * are found.
     *
     * @throws ConfigurationException if the settings are invalid.
     */
    private void verifySettings() throws ConfigurationException {
        try {
            log.debug("Creating server and client to verify configurations");
            getServerFactory().createServer().createClient().checkServerConnection();
        } catch (LanguageModelException e) {
            throw new ConfigurationException(e.getMessage());
        }
    }
}
