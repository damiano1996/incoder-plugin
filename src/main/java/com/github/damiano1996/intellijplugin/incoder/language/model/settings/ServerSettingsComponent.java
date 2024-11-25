package com.github.damiano1996.intellijplugin.incoder.language.model.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.servers.ServerType;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

import static com.github.damiano1996.intellijplugin.incoder.language.model.servers.ServerType.OLLAMA;

@Getter
public class ServerSettingsComponent {

    private final JPanel mainPanel;
    private final ComboBox<ServerType> modelTypeComboBox = new ComboBox<>(ServerType.values());
    private final List<IServerSettingsConfigurable> serverSettings;

    public ServerSettingsComponent(@NotNull List<IServerSettingsConfigurable> serverSettings) {
        this.serverSettings = serverSettings;

        // Container to dynamically swap settings panels
        JPanel dynamicSettingsPanel = new JPanel(new CardLayout());
        serverSettings.forEach((serverSettingsConfigurable) -> dynamicSettingsPanel.add(serverSettingsConfigurable.getComponent().getMainPanel(),
                serverSettingsConfigurable.getDisplayName()));

        // FormBuilder for the main settings UI
        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponent(new TitledSeparator("Language Model Settings"))
                        .setVerticalGap(5)
                        .setFormLeftIndent(20)
                        .addLabeledComponent(new JBLabel("Server type:"), modelTypeComboBox, 1, false)
                        .addVerticalGap(5)
                        .addComponent(dynamicSettingsPanel)
                        .setFormLeftIndent(0)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();

        // Show the selected server type settings panel
        modelTypeComboBox.addItemListener(
                e -> {
                    CardLayout layout = (CardLayout) dynamicSettingsPanel.getLayout();
                    layout.show(dynamicSettingsPanel, modelTypeComboBox.getSelectedItem().toString());
                    mainPanel.revalidate();
                    mainPanel.repaint();
                });

        // Set default server type
        modelTypeComboBox.setSelectedItem(OLLAMA);
    }
}
