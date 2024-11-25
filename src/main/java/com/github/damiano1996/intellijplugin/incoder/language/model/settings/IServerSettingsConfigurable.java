package com.github.damiano1996.intellijplugin.incoder.language.model.settings;

import com.intellij.openapi.options.Configurable;

public interface IServerSettingsConfigurable extends Configurable {

    IServerSettingsComponent getComponent();
}
