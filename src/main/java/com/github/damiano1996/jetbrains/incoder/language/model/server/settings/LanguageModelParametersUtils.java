package com.github.damiano1996.jetbrains.incoder.language.model.server.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

public final class LanguageModelParametersUtils {

    private LanguageModelParametersUtils() {}

    public static Optional<LanguageModelParameters> create(Project project) {
        return new LanguageModelParametersDialog(project).open();
    }

    public static Optional<LanguageModelParameters> edit(
            Project project, LanguageModelParameters original) {
        return new LanguageModelParametersDialog(project, original).open();
    }

    public static @NotNull ComboBox<LanguageModelParameters> getLanguageModelParametersComboBox() {
        ComboBox<LanguageModelParameters> serverNamesComboBox = new ComboBox<>();

        serverNamesComboBox.setRenderer(
                new ListCellRenderer<>() {
                    private final JLabel label = new JLabel();
                    private final JPanel panel = new JPanel(new BorderLayout());

                    {
                        panel.add(label, BorderLayout.LINE_START);
                    }

                    @Override
                    public Component getListCellRendererComponent(
                            JList<? extends LanguageModelParameters> list,
                            LanguageModelParameters value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus) {
                        if (value == null) return new JLabel("");
                        if (index == -1) {
                            label.setText(value.modelName);
                        } else {
                            String text =
                                    String.format(
                                            """
                                            <html>
                                            <b>%s: %s</b>
                                            <ul>
                                                <li>URL: %s</li>
                                                <li>Tokens: %s</li>
                                                <li>Temp: %.2f</li>
                                            </ul>
                                            </html>
                                            """,
                                            value.serverName,
                                            value.modelName,
                                            value.baseUrl,
                                            value.maxTokens,
                                            value.temperature);
                            label.setText(text);
                        }
                        return panel;
                    }
                });

        return serverNamesComboBox;
    }

    public static void refreshComboBoxModels(
            @NotNull ComboBox<LanguageModelParameters> serverNamesComboBox,
            LanguageModelParameters defaultParams) {
        DefaultComboBoxModel<LanguageModelParameters> model =
                (DefaultComboBoxModel<LanguageModelParameters>) serverNamesComboBox.getModel();
        model.removeAllElements();

        List<LanguageModelParameters> configuredModels =
                ServerSettings.getInstance().getState().configuredLanguageModels;

        for (LanguageModelParameters p : configuredModels) {
            model.addElement(p);
        }

        if (defaultParams != null && configuredModels.contains(defaultParams)) {
            serverNamesComboBox.setSelectedItem(defaultParams);
        } else if (!configuredModels.isEmpty()) {
            serverNamesComboBox.setSelectedIndex(0);
        }
    }
}
