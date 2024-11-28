package com.github.damiano1996.intellijplugin.incoder.language.model.server.settings;

import static com.github.damiano1996.intellijplugin.incoder.language.model.server.ServerType.OLLAMA;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.ServerType;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ServerSettingsComponent {

    private final JPanel mainPanel;
    private final ComboBox<ServerType> modelTypeComboBox;
    private final List<ServerConfigurable> serverSettings;

    public ServerSettingsComponent(@NotNull List<ServerConfigurable> serverSettings) {
        this.serverSettings = serverSettings;
        modelTypeComboBox =
                new ComboBox<>(
                        this.serverSettings.stream()
                                .map(ServerConfigurable::getServerType)
                                .toArray(ServerType[]::new));

        JPanel dynamicSettingsPanel = new JPanel(new CardLayout());
        serverSettings.forEach(
                (serverSettingsConfigurable) ->
                        dynamicSettingsPanel.add(
                                serverSettingsConfigurable.getComponent().getMainPanel(),
                                serverSettingsConfigurable.getDisplayName()));

        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponent(new TitledSeparator("Language Model Settings"))
                        .setVerticalGap(5)
                        .setFormLeftIndent(20)
                        .addLabeledComponent(
                                new JBLabel("Server type:"), modelTypeComboBox, 1, false)
                        .addVerticalGap(5)
                        .addComponent(dynamicSettingsPanel)
                        .setFormLeftIndent(0)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();

        modelTypeComboBox.addItemListener(
                e -> {
                    CardLayout layout = (CardLayout) dynamicSettingsPanel.getLayout();
                    layout.show(dynamicSettingsPanel, modelTypeComboBox.getItem().getDisplayName());
                    mainPanel.revalidate();
                    mainPanel.repaint();
                });

        modelTypeComboBox.setSelectedItem(OLLAMA);
    }
}
