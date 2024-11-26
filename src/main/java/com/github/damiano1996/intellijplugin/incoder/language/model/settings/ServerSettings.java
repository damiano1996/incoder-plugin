package com.github.damiano1996.intellijplugin.incoder.language.model.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.servers.ServerType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@Service(Service.Level.APP)
@State(
        name = "ServerSettings",
        storages = {@Storage("InCoderSettings.xml")})
public final class ServerSettings implements PersistentStateComponent<ServerSettings.State> {

    private State state = new State();

    public static ServerSettings getInstance() {
        return ApplicationManager.getApplication().getService(ServerSettings.class);
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    @ToString
    public static class State {
        public ServerType modelType = ServerType.OLLAMA;

    }
}
