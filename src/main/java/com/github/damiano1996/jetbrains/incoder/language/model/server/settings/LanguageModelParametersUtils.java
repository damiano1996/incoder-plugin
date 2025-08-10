package com.github.damiano1996.jetbrains.incoder.language.model.server.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelProjectService;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactoryUtils;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerSettings;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@UtilityClass
public class LanguageModelParametersUtils {

    public Optional<LanguageModelParameters> getLanguageModelParameters(Project project) {
        return new LanguageModelParametersDialog(project).getLanguageModelParameters();
    }

    private class LanguageModelParametersDialog {
        private final Project project;

        private ComboBox<String> serverNameField;
        private ComboBox<String> modelNameField;
        private JTextField baseUrlField;
        private JPasswordField apiKeyField;
        private JSpinner maxTokensSpinner;
        private JSpinner temperatureSpinner;
        private JButton refreshButton;
        private JButton verifyButton;

        public LanguageModelParametersDialog(Project project) {
            this.project = project;
        }

        public Optional<LanguageModelParameters> getLanguageModelParameters() {
            JPanel panel = createMainPanel();

            JOptionPane optionPane =
                    new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = optionPane.createDialog(null, "Add Language Model");

            var okButton = getOkButton(optionPane);
            okButton.setEnabled(false);

            createVerifyButtonListener(okButton);

            dialog.setVisible(true);

            Object result = optionPane.getValue();
            if (result != null && result.equals(JOptionPane.OK_OPTION)) {
                LanguageModelParameters param = getCurrentParameters();

                return Optional.of(param);
            }
            return Optional.empty();
        }

        private void createVerifyButtonListener(JButton okButton) {
            // Verification button action
            verifyButton.addActionListener(
                    e -> {
                        verifyButton.setEnabled(false);

                        CompletableFuture<VerificationResult> futureVerificationResult =
                                new CompletableFuture<>();

                        LanguageModelParameters testParam = getCurrentParameters();

                        verifyButton.setEnabled(false);

                        VerificationResult result =
                                getVerificationResult(testParam, project, futureVerificationResult);

                        if (result.valid) {
                            okButton.setEnabled(true);
                            JOptionPane.showMessageDialog(
                                    null,
                                    result.message,
                                    "Verification Successful",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            okButton.setEnabled(false);
                            JOptionPane.showMessageDialog(
                                    null,
                                    result.message,
                                    "Verification Failed",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                        verifyButton.setEnabled(true);
                    });
        }

        private @NotNull LanguageModelParameters getCurrentParameters() {
            return new LanguageModelParameters(
                    serverNameField.getItem(),
                    modelNameField.getItem(),
                    baseUrlField.getText(),
                    new String(apiKeyField.getPassword()),
                    (Integer) maxTokensSpinner.getValue(),
                    (Double) temperatureSpinner.getValue());
        }

        private JPanel createMainPanel() {
            serverNameField =
                    new ComboBox<>(
                            LanguageModelProjectService.getInstance(
                                            Objects.requireNonNull(
                                                    project,
                                                    "Project must be defined to get available"
                                                            + " server names."))
                                    .getAvailableServerNames()
                                    .toArray(new String[0]));

            modelNameField = new ComboBox<>();
            modelNameField.setEditable(true);
            modelNameField.setPreferredSize(new Dimension(300, 30));

            baseUrlField = new JTextField();
            apiKeyField = new JPasswordField();
            maxTokensSpinner = new JSpinner(new SpinnerNumberModel(2048, 100, 100000, 1));
            temperatureSpinner = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1));

            refreshButton = createRefreshButton();
            verifyButton = new JButton("Verify Connection");

            serverNameField.addItemListener(
                    e -> {
                        if (e.getStateChange() == ItemEvent.DESELECTED) return;

                        String serverName = (String) e.getItem();
                        resetWithDefault(serverName);
                    });

            resetWithDefault(serverNameField.getItem());

            return FormBuilder.createFormBuilder()
                    .setFormLeftIndent(20)
                    .addLabeledComponent(new JBLabel("Server:"), serverNameField, 0, false)
                    .addLabeledComponent(new JBLabel("Base URL:"), baseUrlField, 0, false)
                    .addLabeledComponent(new JBLabel("Api key:"), apiKeyField, 0, false)
                    .addLabeledComponent(
                            new JBLabel("Model name:"),
                            new JPanel() {
                                {
                                    setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
                                    add(modelNameField);
                                    add(refreshButton);
                                }
                            },
                            1,
                            false)
                    .addLabeledComponent(new JBLabel("Temperature:"), temperatureSpinner, 0, false)
                    .addLabeledComponent(new JBLabel("Max tokens:"), maxTokensSpinner, 0, false)
                    .addLabeledComponent(new JBLabel("Verification:"), verifyButton, 0, false)
                    .getPanel();
        }

        private static JButton getOkButton(@NotNull JOptionPane optionPane) {
            for (Component comp : optionPane.getComponents()) {
                if (comp instanceof JPanel buttonPanel) {
                    for (Component button : buttonPanel.getComponents()) {
                        if (button instanceof JButton btn) {
                            if ("OK".equals(btn.getText())) {
                                return btn;
                            }
                        }
                    }
                }
            }
            throw new IllegalArgumentException("No OK button was found in the given option pane.");
        }

        private static VerificationResult getVerificationResult(
                LanguageModelParameters testParam,
                Project activeProject,
                CompletableFuture<VerificationResult> futureVerificationResult) {
            verifyParameters(testParam, activeProject, futureVerificationResult);

            try {
                return futureVerificationResult.get(10, TimeUnit.SECONDS);
            } catch (Exception ex) {
                return new VerificationResult(false, ex.getMessage());
            }
        }

        private static void verifyParameters(
                LanguageModelParameters testParam,
                Project activeProject,
                CompletableFuture<VerificationResult> futureVerificationResult) {
            ProgressManager.getInstance()
                    .runProcessWithProgressSynchronously(
                            () -> {
                                try {
                                    LanguageModelProjectService.getInstance(activeProject)
                                            .verify(testParam);
                                    futureVerificationResult.complete(
                                            new VerificationResult(true, "Parameters are valid."));
                                } catch (Exception ex) {
                                    futureVerificationResult.complete(
                                            new VerificationResult(false, ex.getMessage()));
                                }
                            },
                            "Verifying Parameters",
                            false,
                            activeProject);
        }

        @AllArgsConstructor
        private static class VerificationResult {
            Boolean valid;
            String message;
        }

        private @NotNull JButton createRefreshButton() {
            refreshButton = new JButton(AllIcons.Actions.Refresh);
            refreshButton.setPreferredSize(new Dimension(30, 30));

            refreshButton.addActionListener(
                    e -> {
                        List<String> availableModels;
                        try {
                            availableModels =
                                    ServerFactoryUtils.findByName(serverNameField.getItem())
                                            .createServer()
                                            .getAvailableModels(baseUrlField.getText());
                        } catch (LanguageModelException ex) {
                            availableModels = Collections.emptyList();
                        }
                        modelNameField.removeAllItems();
                        availableModels.forEach(modelNameField::addItem);
                    });
            return refreshButton;
        }

        private void resetWithDefault(String serverName) {
            try {
                LanguageModelParameters defaultParam =
                        ServerFactoryUtils.findByName(serverName)
                                .createServer()
                                .getDefaultParameters();
                baseUrlField.setText(defaultParam.baseUrl);
                apiKeyField.setText(defaultParam.apiKey);
                modelNameField.setSelectedItem(defaultParam.modelName);
                temperatureSpinner.setValue(defaultParam.temperature);
                maxTokensSpinner.setValue(defaultParam.maxTokens);

            } catch (LanguageModelException ex) {
                log.warn("Unable to load default params", ex);
            }
        }
    }

    public @NotNull ComboBox<LanguageModelParameters> getLanguageModelParametersComboBox() {
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

    public void refreshModels(
            @NotNull ComboBox<LanguageModelParameters> serverNamesComboBox,
            LanguageModelParameters defaultParams) {
        DefaultComboBoxModel<LanguageModelParameters> model =
                (DefaultComboBoxModel<LanguageModelParameters>) serverNamesComboBox.getModel();
        model.removeAllElements();

        List<LanguageModelParameters> updatedModels =
                ServerSettings.getInstance().getState().configuredLanguageModels;

        for (LanguageModelParameters param : updatedModels) {
            model.addElement(param);
        }

        if (defaultParams != null && updatedModels.contains(defaultParams)) {
            serverNamesComboBox.setSelectedItem(defaultParams);
        } else if (!updatedModels.isEmpty()) {
            serverNamesComboBox.setSelectedIndex(0);
        }
    }
}
