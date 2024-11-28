package com.github.damiano1996.intellijplugin.incoder.language.model.server.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.ServerType;
import com.intellij.openapi.options.Configurable;

public interface ServerConfigurable extends Configurable {

    ServerType getServerType();

    ServerComponent getComponent();
}
