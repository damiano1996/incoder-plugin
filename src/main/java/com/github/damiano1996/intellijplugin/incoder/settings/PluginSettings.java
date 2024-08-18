package com.github.damiano1996.intellijplugin.incoder.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Service(Service.Level.APP)
@State(
        name = "PluginSettings",
        storages = {@Storage("InCoderSettings.xml")})
public final class PluginSettings implements PersistentStateComponent<PluginSettings.State> {

    private State state = new State();

    public static PluginSettings getInstance() {
        return ApplicationManager.getApplication().getService(PluginSettings.class);
    }

    @Override
    public PluginSettings.State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull PluginSettings.State state) {
        this.state = state;
    }

    @ToString
    public static class State {
        public boolean isFirstPluginRun = true;
    }
}
