package com.github.damiano1996.intellijplugin.incoder.llm.server.settings;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;

public interface CustomServerSettingsComponent {

    JPanel getMainPanel();

    ServerSettings.State.ServerType getServerType();

    Configurable getConfigurable();
}
