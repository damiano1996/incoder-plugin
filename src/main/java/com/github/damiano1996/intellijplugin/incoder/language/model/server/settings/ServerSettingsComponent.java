package com.github.damiano1996.intellijplugin.incoder.language.model.server.settings;

import com.github.damiano1996.intellijplugin.incoder.language.model.server.LanguageModelServerType;
import com.github.damiano1996.intellijplugin.incoder.settings.description.label.DescriptionLabel;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.options.newEditor.SettingsDialog;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.FormBuilder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.util.Arrays;

@Getter
public class ServerSettingsComponent {

    private final JPanel mainPanel;
    private final ComboBox<LanguageModelServerType> serverTypeComboBox = new ComboBox<>(LanguageModelServerType.values());
    private final JPanel linksPanel = new JPanel(new VerticalLayout(5));

    public ServerSettingsComponent() {

        initializeChildLinks();
        configureComboBoxRenderer();

        mainPanel =
                FormBuilder.createFormBuilder()
                        .setFormLeftIndent(20)
                        .addLabeledComponent(
                                new JBLabel("Server type:"), serverTypeComboBox, 1, false)
                        .addComponent(new DescriptionLabel("Select the server to be used for interaction with language models."))
                        .addSeparator(20)
                        .addComponent(new JBLabel("Language model provider settings:"))
                        .addComponent(new DescriptionLabel("Configure settings for the selected language model provider using the links below."))
                        .addComponent(linksPanel)
                        .setFormLeftIndent(0)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();

        serverTypeComboBox.setSelectedItem(LanguageModelServerType.OLLAMA);
    }

    private void configureComboBoxRenderer() {
        serverTypeComboBox.setRenderer(new ListCellRenderer<>() {
            private final JLabel label = new JLabel();

            @Override
            public Component getListCellRendererComponent(JList<? extends LanguageModelServerType> list, LanguageModelServerType value, int index, boolean isSelected, boolean cellHasFocus) {
                label.setText(value != null ? value.getDisplayName() : "");
                label.setOpaque(true);
                label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                return label;
            }
        });
    }

    private void initializeChildLinks() {
        for (Configurable child : Arrays.stream(LanguageModelServerType.values()).map(serverFactory -> serverFactory.getServerFactory().createConfigurable()).toList()) {
            HyperlinkLabel link = new HyperlinkLabel(child.getDisplayName());
            link.addHyperlinkListener(e -> {
                // TODO: How to navigate between setting pages?
            });
            linksPanel.add(link);
        }
    }
}
