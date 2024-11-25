package com.github.damiano1996.intellijplugin.incoder.language.model.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.servers.ServerType;
import com.intellij.openapi.options.Configurable;

public interface ServerConfigurable extends Configurable {

    ServerType getServerType();

    ServerComponent getComponent();
}
