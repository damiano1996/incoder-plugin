package com.github.damiano1996.intellijplugin.incoder.llm.server.container.settings;

import com.github.damiano1996.intellijplugin.incoder.InCoderBundle;
import com.github.damiano1996.intellijplugin.incoder.llm.server.container.orchestration.orchestrators.OrchestratorType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import java.io.File;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Service(Service.Level.APP)
@State(
        name = "ContainerSettings",
        storages = {@Storage("InCoderSettings.xml")})
public final class ContainerSettings implements PersistentStateComponent<ContainerSettings.State> {

    private State state = new State();

    public static ContainerSettings getInstance() {
        return ApplicationManager.getApplication().getService(ContainerSettings.class);
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    @ToString
    public static class State {

        public OrchestratorType orchestratorType = OrchestratorType.DOCKER;

        public String imageName = "incoder-service";
        public String imageTag = "latest";

        public String containerName = "incoder-service";
        public int containerPortNumber = 8000;
        public int localHostPortNumber = 8899;

        public boolean cuda = true;

        public HuggingFace huggingFace = new HuggingFace();

        @ToString
        public static class HuggingFace {
            public String home =
                    System.getProperty("user.home")
                            + File.separator
                            + ".cache"
                            + File.separator
                            + "huggingface";
            public String hubCache =
                    System.getProperty("user.home")
                            + File.separator
                            + ".cache"
                            + File.separator
                            + "huggingface"
                            + File.separator
                            + "hub";
            public Model model = Model.FACEBOOK_INCODER_1B;

            @Getter
            public enum Model {
                FACEBOOK_INCODER_1B(
                        "facebook/incoder-1B",
                        "Facebook InCoder 1B",
                        InCoderBundle.message("model--facebook--incoder-1B-description")),
                FACEBOOK_INCODER_6B(
                        "facebook/incoder-6B",
                        "Facebook InCoder 6B",
                        InCoderBundle.message("model--facebook--incoder-6B-description"));

                private final String repository;
                private final String name;
                private final String description;

                Model(String repository, String name, String description) {
                    this.repository = repository;
                    this.name = name;
                    this.description = description;
                }
            }
        }
    }
}
