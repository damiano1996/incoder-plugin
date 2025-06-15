package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelServiceImpl;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.ThrowableComputable;
import java.util.Objects;
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
        updateState();

        var serverName = getServerFactory().getName();

        if (ServerSettings.getInstance().getState().activeServerName.equals(serverName)) {
            log.debug(
                    "{} is already the active server. Just restarting the Language Model"
                            + " Service...",
                    serverName);
            restartLanguageModelService(serverName);
            return;
        }

        log.debug("{} is not the active server. Showing the dialog to the user...", serverName);
        showDialogToSetThisAsDefaultServer(serverName);
    }

    private void showDialogToSetThisAsDefaultServer(String serverName)
            throws ConfigurationException {
        //noinspection DialogTitleCapitalization
        int result =
                Messages.showYesNoDialog(
                        "Do you want to set %s as the default server?".formatted(serverName),
                        "Set %s as Default".formatted(serverName),
                        Messages.getQuestionIcon());

        if (result == Messages.YES) {
            log.info("User chose to set {} as default.", serverName);
            ServerSettings.getInstance().getState().activeServerName = getServerFactory().getName();

            restartLanguageModelService(serverName);

        } else {
            log.info("User chose not to set {} as default.", serverName);
        }
    }

    private void restartLanguageModelService(String serverName) throws ConfigurationException {
        //noinspection DialogTitleCapitalization
        ProgressManager.getInstance()
                .runProcessWithProgressSynchronously(
                        (ThrowableComputable<Void, ConfigurationException>)
                                () -> {
                                    try {
                                        log.debug("Restarting Language Model Service");
                                        LanguageModelServiceImpl.getInstance(
                                                        Objects.requireNonNull(
                                                                ProjectUtil.getActiveProject()))
                                                .init(getServerFactory().createServer());
                                        return null;
                                    } catch (LanguageModelException e) {
                                        //noinspection DialogTitleCapitalization
                                        throw new ConfigurationException(
                                                e.getMessage(),
                                                "Unable to Initialize the Language Model Service with New Settings for %s"
                                                        .formatted(serverName));
                                    }
                                },
                        "Restarting the Language Model Service with the New Settings for %s"
                                .formatted(getDisplayName()),
                        false,
                        null);
    }

    /** Updates the server state based on the current configuration. */
    protected abstract void updateState();
}
