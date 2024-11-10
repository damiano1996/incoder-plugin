package com.github.damiano1996.intellijplugin.incoder.llm.server.settings;

import com.github.damiano1996.intellijplugin.incoder.llm.container.server.settings.ContainerSettingsComponent;
import com.github.damiano1996.intellijplugin.incoder.llm.langchain.server.settings.LangChainSettingsComponent;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import java.awt.event.ItemEvent;
import java.util.List;
import javax.swing.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ServerSettingsComponent {

    private final JPanel mainPanel;
    private final ComboBox<ServerSettings.State.ServerType> serverTypeComboBox;

    private final List<CustomServerSettingsComponent> customServerSettingsComponents =
            List.of(new ContainerSettingsComponent(), new LangChainSettingsComponent());

    public ServerSettingsComponent() {
        serverTypeComboBox = new ComboBox<>(ServerSettings.State.ServerType.values());

        List<JPanel> panels =
                customServerSettingsComponents.stream()
                        .map(CustomServerSettingsComponent::getMainPanel)
                        .toList();

        updateVisibility(serverTypeComboBox.getItem());

        serverTypeComboBox.addItemListener(
                event -> {
                    updateVisibility(event);
                    revalidatePanels();
                });

        var formBuilder =
                FormBuilder.createFormBuilder()
                        .addComponent(new TitledSeparator("Server Settings"))
                        .setVerticalGap(5)
                        .setFormLeftIndent(20)
                        .addLabeledComponent(new JBLabel("Type:"), serverTypeComboBox, 1, false)
                        .setFormLeftIndent(0);

        panels.forEach(formBuilder::addComponent);

        mainPanel = formBuilder.addComponentFillVertically(new JPanel(), 0).getPanel();
    }

    private void updateVisibility(@NotNull ItemEvent e) {
        var selected = (ServerSettings.State.ServerType) e.getItem();
        updateVisibility(selected);
    }

    private void updateVisibility(ServerSettings.State.ServerType serverType) {
        customServerSettingsComponents.forEach(
                customServerSettingsComponent -> {
                    customServerSettingsComponent
                            .getMainPanel()
                            .setVisible(
                                    customServerSettingsComponent
                                            .getServerType()
                                            .equals(serverType));
                });
    }

    private void revalidatePanels() {
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
