package com.github.damiano1996.jetbrains.incoder.language.model.server;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelService;
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


        // Show dialog to ask user if they want to set the server as default
        int result = Messages.showYesNoDialog(
                "Do you want to set this server as the default?",
                "Set Server as Default",
                Messages.getQuestionIcon()
        );

        // Handle the user's response
        if (result == Messages.YES) {
            log.info("User chose to set the server as default.");
            ServerSettings.getInstance().getState().activeServerName = getServerFactory().getName();

            //noinspection DialogTitleCapitalization
            ProgressManager.getInstance()
                    .runProcessWithProgressSynchronously(
                            (ThrowableComputable<Void, ConfigurationException>)
                                    () -> {

                                        restartLanguageModelService();

                                        return null;
                                    },
                            "Restarting Language Model Service with Updated Settings for %s".formatted(getDisplayName()),
                            false,
                            null);

        } else {
            log.info("User chose not to set the server as default.");
        }
    }

    /** Updates the server state based on the current configuration. */
    protected abstract void updateState();

    private void restartLanguageModelService() throws ConfigurationException {
        try {
            LanguageModelService.getInstance(Objects.requireNonNull(ProjectUtil.getActiveProject()))
                    .init(getServerFactory().createServer());
        } catch (LanguageModelException e) {
            throw new ConfigurationException(
                    e.getMessage(), "Unable to Initialize the Language Model Service");
        }
    }
}
