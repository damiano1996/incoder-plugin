package com.github.damiano1996.intellijplugin.incoder.llm.container.server.orchestration.orchestrators.docker;

import com.github.damiano1996.intellijplugin.incoder.llm.container.server.Container;
import com.github.damiano1996.intellijplugin.incoder.llm.container.server.ContainerException;
import com.github.damiano1996.intellijplugin.incoder.llm.container.server.ContainerState;
import com.github.damiano1996.intellijplugin.incoder.llm.container.server.orchestration.orchestrators.ContainerOrchestrator;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class DockerContainerOrchestrator implements ContainerOrchestrator {
    private final DockerClient dockerClient;

    public DockerContainerOrchestrator() {
        this.dockerClient = DockerClientBuilder.getInstance().build();
    }

    private String @NotNull [] envVariablesToList(@NotNull Map<String, String> envVariables) {
        return envVariables.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .toArray(String[]::new);
    }

    @Override
    public void pull(String name, String version) throws ContainerException {
        try {
            dockerClient
                    .pullImageCmd("%s:%s".formatted(name, version))
                    .exec(new PullImageResultCallback())
                    .awaitCompletion();
        } catch (InterruptedException e) {
            throw new ContainerException(
                    "Unable to pull the image: %s:%s".formatted(name, version), e);
        }
    }

    @Override
    public Container start(@NotNull Container container) throws ContainerException {

        try {
            if (container.getName() != null) removeExistingContainers(container.getName());

            String id = createContainerIdentifier(container);
            Container containerWithId = container.withId(id);
            log.debug("Container with id: {}", containerWithId);

            assert containerWithId.getId() != null;
            dockerClient.startContainerCmd(containerWithId.getId()).exec();
            log.debug("Docker container started with id: {}", containerWithId.getId());

            return containerWithId;
        } catch (Exception e) {
            throw new ContainerException("Unable to start the Docker container.", e);
        }
    }

    private void removeExistingContainers(String containerName) {
        var existingContainers =
                dockerClient
                        .listContainersCmd()
                        .withNameFilter(List.of(containerName))
                        .withShowAll(true)
                        .exec();

        existingContainers.forEach(
                container -> {
                    log.debug("Stopping and removing container with id: {}", container.getId());
                    String containerId = container.getId();
                    try {
                        dockerClient.stopContainerCmd(containerId).exec();
                    } catch (NotModifiedException e) {
                        log.debug("Ignoring: {}", e.getMessage());
                    }
                    dockerClient.removeContainerCmd(containerId).exec();
                });
    }

    private String createContainerIdentifier(@NotNull Container container) {
        log.debug("Going to create Docker container command. Configs: {}", container);
        CreateContainerCmd createContainerCmd = getCreateContainerCmd(container);

        if (container.getName() != null) {
            log.debug("Adding container name to command configs");
            createContainerCmd.withName(container.getName());
        }

        log.debug("Executing container creation");
        CreateContainerResponse response = createContainerCmd.exec();

        return response.getId();
    }

    @Override
    public void restart(@NotNull Container container) throws ContainerException {
        if (container.getId() == null)
            throw new ContainerException("Container id must be defined to restart the container.");

        try {
            log.debug("Restarting container: {}", container);
            dockerClient.restartContainerCmd(container.getId()).exec();
            log.debug("Container restarted");
        } catch (Exception e) {
            throw new ContainerException("Unable to restart the container.", e);
        }
    }

    private CreateContainerCmd getCreateContainerCmd(@NotNull Container container) {
        return dockerClient
                .createContainerCmd(
                        "%s:%s"
                                .formatted(
                                        container.getImage().getName(),
                                        container.getImage().getVersion()))
                .withExposedPorts(ExposedPort.tcp(container.getPort()))
                .withHostConfig(
                        HostConfig.newHostConfig()
                                .withPortBindings(
                                        new PortBinding(
                                                Ports.Binding.bindPort(container.getHostPort()),
                                                ExposedPort.tcp(container.getPort())))
                                .withBinds(
                                        container.getBinds().stream()
                                                .map(
                                                        bind ->
                                                                new Bind(
                                                                        bind.getPath(),
                                                                        new Volume(
                                                                                bind.getVolume())))
                                                .toList())
                                .withDevices(
                                        container.getDevices().stream()
                                                .map(
                                                        device ->
                                                                new Device(
                                                                        device
                                                                                .getCGroupPermissions(),
                                                                        device.getPathInContainer(),
                                                                        device.getPathOnHost()))
                                                .toList())
                                .withRuntime(container.getRuntime()))
                .withEnv(envVariablesToList(container.getEnvVariables()));
    }

    public ContainerState getContainerState(@NotNull Container container)
            throws ContainerException {
        if (container.getId() == null)
            throw new ContainerException("Container id must be defined.");

        log.debug("Getting Docker container state...");
        var response = dockerClient.inspectContainerCmd(container.getId()).exec();

        var state = response.getState();
        log.debug("Docker container response state: {}", state);

        return ContainerState.builder()
                .status(
                        ContainerState.Status.valueOf(
                                Objects.requireNonNull(
                                                state.getStatus(),
                                                "Docker container status was null.")
                                        .toUpperCase()))
                .errorMessage(state.getError())
                .build();
    }

    @Override
    public void stopContainer(@NotNull Container container) throws ContainerException {
        if (container.getId() == null)
            throw new ContainerException("Container id is required to stop the container.");

        try {
            log.debug("Stopping Docker container: {}", container);
            dockerClient.stopContainerCmd(container.getId()).exec();
            log.debug("Container stopped");
        } catch (Exception e) {
            throw new ContainerException("Unable to stop the container.", e);
        }
    }
}
