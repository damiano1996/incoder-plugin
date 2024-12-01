package com.github.damiano1996.intellijplugin.incoder.language.model.server.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.ServerFactory;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.ServerFactoryUtils;
import com.github.damiano1996.intellijplugin.incoder.ui.components.DescriptionLabel;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.FormBuilder;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;

@Getter
public class ServerSettingsComponent {

    private final JPanel mainPanel;
    private final ComboBox<String> serverTypeComboBox;
    private final JPanel linksPanel = new JPanel(new VerticalLayout(5));

    public ServerSettingsComponent() {

        serverTypeComboBox =
                new ComboBox<>(
                        ServerFactoryUtils.getServerFactories().stream()
                                .map(ServerFactory::getName)
                                .toList()
                                .toArray(new String[0]));

        mainPanel =
                FormBuilder.createFormBuilder()
                        .setFormLeftIndent(20)
                        .addLabeledComponent(
                                new JBLabel("Server type:"), serverTypeComboBox, 1, false)
                        .addComponent(
                                new DescriptionLabel(
                                        "Select the server to be used for interaction with language"
                                                + " models."))
                        .setFormLeftIndent(0)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();
    }
}
