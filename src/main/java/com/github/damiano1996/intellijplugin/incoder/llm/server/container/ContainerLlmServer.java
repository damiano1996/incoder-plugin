package com.github.damiano1996.intellijplugin.incoder.llm.server.container;

import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableException;
import com.github.damiano1996.intellijplugin.incoder.initializable.InitializableListener;
import com.github.damiano1996.intellijplugin.incoder.llm.server.LlmServer;
import com.github.damiano1996.intellijplugin.incoder.llm.server.ServerException;
import com.github.damiano1996.intellijplugin.incoder.llm.server.container.orchestration.OrchestratorFactoryImpl;
import com.github.damiano1996.intellijplugin.incoder.llm.server.container.orchestration.orchestrators.ContainerOrchestrator;
import com.github.damiano1996.intellijplugin.incoder.llm.server.container.settings.ContainerSettings;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ContainerLlmServer implements LlmServer {

    private static final String CONTAINER_HF_HOME = "/app/.cache/huggingface";
    private static final String CONTAINER_HF_HUB_CACHE = "/app/.cache/huggingface/hub";

    private final ContainerOrchestrator containerOrchestrator;
    private final List<InitializableListener> listeners = new ArrayList<>();
    private final com.github.damiano1996.intellijplugin.incoder.client.api.DefaultApi client;

    private Container container;

    public ContainerLlmServer() {
        containerOrchestrator =
                new OrchestratorFactoryImpl().create(getSettingsState().orchestratorType);
        client = new com.github.damiano1996.intellijplugin.incoder.client.api.DefaultApi();
    }

    private static ContainerSettings.@NotNull State getSettingsState() {
        return Objects.requireNonNull(
                ContainerSettings.getInstance().getState(), "Container settings must be defined.");
    }

    @Override
    public URL getBaseUrl() throws ServerException {
        try {
            String baseUrl =
                    "http://localhost:%s".formatted(getSettingsState().localHostPortNumber);
            log.debug("Server base url: {}", baseUrl);
            return new URL(baseUrl);
        } catch (MalformedURLException e) {
            throw new ServerException(e);
        }
    }

    @Override
    public void subscribe(InitializableListener listener) {
        listeners.add(listener);
        log.debug("Listener subscribed");
    }

    @Override
    public void init() throws InitializableException {
        notify("Initializing the container...");

        initContainerConfigs();

        pullImage();

        startContainer();
        waitContainer();
        loadLargeLanguageModel();

        notify("Container is ready");
    }

    private void pullImage() throws InitializableException {
        try {
            notify("Pulling image %s:%s...".formatted(container.getImage().getName(), container.getImage().getVersion()));
            containerOrchestrator.pull(
                    container.getImage().getName(), container.getImage().getVersion());
            notify("Image pulled successfully");
        } catch (ContainerException e) {
            throw new InitializableException("Unable to pull image.", e);
        }
    }

    private void notify(String message) {
        listeners.forEach(listener -> listener.onStatusUpdate(message));
        log.debug("Listeners notified with message: {}", message);
    }

    private void initContainerConfigs() {
        notify("Preparing container settings");
        var settingsState = getSettingsState();

        Container.ContainerBuilder containerBuilder =
                Container.builder()
                        .image(new Container.Image(settingsState.imageName, settingsState.imageTag))
                        .name(settingsState.containerName)
                        .hostPort(settingsState.localHostPortNumber)
                        .port(settingsState.containerPortNumber)
                        .binds(
                                List.of(
                                        new Container.Bind(
                                                settingsState.huggingFace.home, CONTAINER_HF_HOME),
                                        new Container.Bind(
                                                settingsState.huggingFace.hubCache,
                                                CONTAINER_HF_HUB_CACHE)))
                        .envVariables(
                                Map.of(
                                        "BIG_MODEL",
                                        String.valueOf(
                                                settingsState.huggingFace.model.equals(
                                                        ContainerSettings.State.HuggingFace.Model
                                                                .FACEBOOK_INCODER_6B)),
                                        "CUDA",
                                        String.valueOf(settingsState.cuda),
                                        "HF_HOME",
                                        CONTAINER_HF_HOME,
                                        "HF_HUB_CACHE",
                                        CONTAINER_HF_HUB_CACHE));

        if (settingsState.cuda) {
            setCudaSettings(containerBuilder);
        }

        container = containerBuilder.build();
    }

    private void setCudaSettings(Container.@NotNull ContainerBuilder containerBuilder) {
        containerBuilder
                .runtime("nvidia")
                .devices(
                        List.of(
                                new Container.Device("rwm", "/dev/nvidia0", "/dev/nvidia0"),
                                new Container.Device("rwm", "/dev/nvidiactl", "/dev/nvidiactl"),
                                new Container.Device("rwm", "/dev/nvidia-uvm", "/dev/nvidia-uvm"),
                                new Container.Device(
                                        "rwm", "/dev/nvidia-uvm-tools", "/dev/nvidia-uvm-tools")));
    }

    private void startContainer() throws InitializableException {
        notify("Starting container...");
        try {
            container =
                    containerOrchestrator.start(
                            Objects.requireNonNull(container, "Container must be configured."));
        } catch (ContainerException e) {
            throw new InitializableException("Unable to start local container.", e);
        }
        notify("Container started");
    }

    private void waitContainer() throws InitializableException {
        notify("Initializing the container");
        Objects.requireNonNull(container, "Container must be configured.");

        CompletableFuture<Void> containerReadyFuture =
                CompletableFuture.runAsync(
                        () -> {
                            while (true) {
                                try {
                                    ContainerState state =
                                            containerOrchestrator.getContainerState(container);
                                    log.debug("Container state: {}", state);
                                    notify(
                                            "Container state: %s"
                                                    .formatted(
                                                            state.getStatus()
                                                                    .name()
                                                                    .toLowerCase(Locale.ROOT)));

                                    if (state.getStatus() == ContainerState.Status.RUNNING) {
                                        log.debug("Exit the loop. The container is running");
                                        return;
                                    }
                                } catch (ContainerException e) {
                                    log.warn("Error checking container state: {}", e.getMessage());
                                }

                                try {
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    throw new RuntimeException(
                                            "Thread interrupted while waiting for container to be"
                                                    + " ready",
                                            e);
                                }
                            }
                        });

        try {
            containerReadyFuture.get(5, TimeUnit.MINUTES); // Wait for up to 10 minutes
            notify("Container is up and running");
        } catch (Exception e) {
            throw new InitializableException(
                    "Failed to initialize the container within the timeout period", e);
        }
    }

    private void loadLargeLanguageModel() throws InitializableException {
        notify("Initializing the large language model");
        try {
            client.setCustomBaseUrl(getBaseUrl().toString());
        } catch (ServerException e) {
            throw new InitializableException(e);
        }

        CompletableFuture<Void> modelReadyFuture =
                CompletableFuture.runAsync(
                        () -> {
                            while (true) {
                                try {
                                    com.github.damiano1996.intellijplugin.incoder.client.model
                                                    .HealthCheckResponse
                                            healthCheckResponse =
                                                    client.healthCheckApiV1HealthGet();
                                    log.debug("Health check response: {}", healthCheckResponse);

                                    switch (Objects.requireNonNull(
                                                    healthCheckResponse.getModelStatus())
                                            .toString()) {
                                        case "INITIALIZING":
                                            notify("Initializing the large language model");
                                            break;
                                        case "DOWNLOADING":
                                            notify("Downloading the large language model");
                                            break;
                                        case "ERROR":
                                            throw new RuntimeException(
                                                    "Unable to initialize the large language"
                                                            + " model.");
                                        case "READY":
                                            notify("The large language model is ready");
                                            log.debug("Exit the loop. The model is ready");
                                            return;
                                    }
                                } catch (
                                        com.github.damiano1996.intellijplugin.incoder.client.invoker
                                                        .ApiException
                                                e) {
                                    log.warn("Connection exception: {}", e.getMessage());
                                }

                                try {
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    throw new RuntimeException(
                                            "Thread interrupted while waiting for model to be"
                                                    + " ready",
                                            e);
                                }
                            }
                        });

        try {
            modelReadyFuture.get(10, TimeUnit.MINUTES);
            notify("Large language model is ready to process requests");
        } catch (Exception e) {
            throw new InitializableException(
                    "Failed to initialize the large language model within the timeout period", e);
        }
    }

    @Override
    public void close() {
        if (container != null) {
            try {
                containerOrchestrator.stopContainer(container);
            } catch (ContainerException e) {
                log.error("Unable to stop the container.", e);
            }
        }
    }
}
