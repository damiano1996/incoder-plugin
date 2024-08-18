package com.github.damiano1996.intellijplugin.incoder.llm.server.settings;

import com.github.damiano1996.intellijplugin.incoder.llm.server.container.settings.ContainerSettingsComponent;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;
import lombok.Getter;

@Getter
public class ServerSettingsComponent {

    private final JPanel mainPanel;
    private final ComboBox<ServerSettings.State.ServerType> serverTypeComboBox;

    private final ContainerSettingsComponent containerSettingsComponent;

    public ServerSettingsComponent() {
        serverTypeComboBox = new ComboBox<>(ServerSettings.State.ServerType.values());

        containerSettingsComponent = new ContainerSettingsComponent();

        var containerPanel = containerSettingsComponent.getMainPanel();

        serverTypeComboBox.addItemListener(
                e -> {
                    var enable = e.getItem().equals(ServerSettings.State.ServerType.LOCAL);
                    containerPanel.setEnabled(enable);
                    for (int i = 0; i < containerPanel.getComponents().length; i++) {
                        containerPanel.getComponent(i).setEnabled(enable);
                    }
                });

        // Create the main panel using FormBuilder
        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponent(new TitledSeparator("Server Settings"))
                        .setVerticalGap(5)
                        .setFormLeftIndent(20)
                        .addLabeledComponent(new JBLabel("Location:"), serverTypeComboBox, 1, false)
                        .addComponentFillVertically(new JPanel(), 0)
                        .setFormLeftIndent(0)
                        .addComponent(containerPanel)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();
    }
}
