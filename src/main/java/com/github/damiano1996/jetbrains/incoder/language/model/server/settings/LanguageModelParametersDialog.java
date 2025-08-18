package com.github.damiano1996.jetbrains.incoder.language.model.server.settings;

import static com.github.damiano1996.jetbrains.incoder.language.model.server.ollama.OllamaLanguageModelServer.OLLAMA;

import com.github.damiano1996.jetbrains.incoder.ClassInstantiator;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelProjectService;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ProviderUIStrategy;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactoryUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.FormBuilder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

@Slf4j
final class LanguageModelParametersDialog {

    private final Project project;
    @Nullable private final LanguageModelParameters currentParameters;

    private JPanel providerPanel;
    private CardLayout providerCards;
    private final Map<String, ProviderUIStrategy> strategies = new LinkedHashMap<>();

    private ComboBox<String> serverNameField;
    private ComboBox<String> modelNameField;
    private JTextField baseUrlField;
    private JPasswordField apiKeyField;
    private JSpinner maxTokensSpinner;
    private JSpinner temperatureSpinner;
    private JTextField stopSequencesField;
    private JSpinner timeoutSpinner;

    private JButton refreshButton;
    private JButton verifyButton;

    LanguageModelParametersDialog(Project project) {
        this.project = project;
        this.currentParameters = null;
    }

    LanguageModelParametersDialog(
            Project project, @NonNull LanguageModelParameters currentParameters) {
        this.project = project;
        this.currentParameters = currentParameters;
    }

    Optional<LanguageModelParameters> open() {
        JPanel panel = createMainPanel();

        JOptionPane optionPane =
                new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog =
                optionPane.createDialog(
                        null,
                        currentParameters != null ? "Edit Language Model" : "Add Language Model");

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

    private void initStrategiesAndCards() {
        strategies.clear();

        ClassInstantiator.findImplementations(ProviderUIStrategy.class)
                .forEach(
                        providerUIStrategy -> {
                            strategies.put(
                                    normalize(providerUIStrategy.name()), providerUIStrategy);
                            providerPanel.add(
                                    providerUIStrategy.buildPanel(), providerUIStrategy.cardName());
                        });
    }

    private ProviderUIStrategy currentStrategy() {
        String key = normalize(serverNameField.getItem());
        return strategies.getOrDefault(key, strategies.values().iterator().next());
    }

    private static @NotNull String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    private JPanel createMainPanel() {
        providerCards = new CardLayout();
        providerPanel = new JPanel(providerCards);

        serverNameField =
                new ComboBox<>(
                        LanguageModelProjectService.getInstance(
                                        Objects.requireNonNull(project, "Project must be defined"))
                                .getAvailableServerNames()
                                .toArray(new String[0]));

        modelNameField = new ComboBox<>();
        modelNameField.setEditable(true);
        modelNameField.setPreferredSize(new Dimension(300, 30));

        baseUrlField = new JTextField();
        baseUrlField.setColumns(40);

        apiKeyField = new JPasswordField();
        apiKeyField.setColumns(40);

        maxTokensSpinner = new JSpinner(new SpinnerNumberModel(2048, 100, 100000, 1));
        temperatureSpinner = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 2.0, 0.1));
        stopSequencesField = new JTextField();
        timeoutSpinner = new JSpinner(new SpinnerNumberModel(60, 1, 3600, 1));

        refreshButton = createRefreshButton();
        verifyButton = new JButton("Verify Connection");

        initStrategiesAndCards();

        serverNameField.addItemListener(
                e -> {
                    if (e.getStateChange() == ItemEvent.DESELECTED) return;
                    String serverName = (String) e.getItem();
                    providerCards.show(providerPanel, normalize(serverName).toUpperCase());
                    boolean apiKeyRequired = !normalize(serverName).equals(OLLAMA);
                    apiKeyField.setEnabled(apiKeyRequired);
                    apiKeyField.setEditable(apiKeyRequired);
                    applyDefaultParameters(serverName);
                });

        if (currentParameters != null) {
            applyModelParameters(currentParameters);
        } else {
            applyDefaultParameters(serverNameField.getItem());
        }

        providerCards.show(providerPanel, normalize(serverNameField.getItem()).toUpperCase());

        var form =
                FormBuilder.createFormBuilder()
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
                                0,
                                false)
                        .addLabeledComponent(
                                new JBLabel("Temperature:"), temperatureSpinner, 0, false)
                        .addLabeledComponent(
                                new JBLabel("Stop sequences (CSV):"), stopSequencesField, 0, false)
                        .addLabeledComponent(new JBLabel("Timeout (s):"), timeoutSpinner, 0, false)
                        .addLabeledComponent(new JBLabel("Max tokens:"), maxTokensSpinner, 0, false)
                        .addComponent(new JSeparator())
                        .addLabeledComponent(
                                new JBLabel("Provider settings:"), providerPanel, 0, false)
                        .addLabeledComponent(new JBLabel("Verification:"), verifyButton, 0, false)
                        .getPanel();

        JScrollPane scrollPane =
                new JBScrollPane(
                        form,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private void applyDefaultParameters(@UnknownNullability String serverName) {
        try {
            LanguageModelParameters defaultParameters =
                    ServerFactoryUtils.findByName(serverName).createServer().getDefaultParameters();
            applyModelParameters(defaultParameters);
        } catch (LanguageModelException e) {
            log.warn("Unable to apply default parameters");
        }
    }

    private @NotNull LanguageModelParameters getCurrentParameters() {
        LanguageModelParameters base =
                new LanguageModelParameters(
                        serverNameField.getItem(),
                        modelNameField.getItem(),
                        baseUrlField.getText(),
                        new String(apiKeyField.getPassword()),
                        (Integer) maxTokensSpinner.getValue(),
                        (Double) temperatureSpinner.getValue(),
                        parseStopSequences(stopSequencesField.getText()),
                        java.time.Duration.ofSeconds((Integer) timeoutSpinner.getValue()));
        return currentStrategy().collect(base);
    }

    private void applyModelParameters(@NotNull LanguageModelParameters modelParameters) {
        baseUrlField.setText(modelParameters.baseUrl);
        apiKeyField.setText(modelParameters.apiKey);
        modelNameField.setSelectedItem(modelParameters.modelName);

        temperatureSpinner.setValue(
                modelParameters.temperature != null ? modelParameters.temperature : 0.5);
        maxTokensSpinner.setValue(
                modelParameters.maxTokens != null ? modelParameters.maxTokens : 2048);

        stopSequencesField.setText(
                modelParameters.stopSequences != null && !modelParameters.stopSequences.isEmpty()
                        ? String.join(",", modelParameters.stopSequences)
                        : "");
        timeoutSpinner.setValue(
                modelParameters.timeout != null
                        ? Math.max(1, (int) modelParameters.timeout.getSeconds())
                        : 60);

        currentStrategy().applyDefaults(modelParameters);
    }

    private static java.util.@NotNull List<String> parseStopSequences(String csv) {
        if (csv == null || csv.trim().isEmpty()) return java.util.Collections.emptyList();
        String[] parts = csv.split(",");
        java.util.List<String> out = new java.util.ArrayList<>(parts.length);
        for (String p : parts) {
            String s = p.trim();
            if (!s.isEmpty()) out.add(s);
        }
        return out;
    }

    private void createVerifyButtonListener(JButton okButton) {
        verifyButton.addActionListener(
                e -> {
                    verifyButton.setEnabled(false);

                    CompletableFuture<VerificationResult> futureVerificationResult =
                            new CompletableFuture<>();

                    LanguageModelParameters testParam = getCurrentParameters();

                    verifyButton.setEnabled(false);

                    VerificationResult result =
                            getVerificationResult(testParam, project, futureVerificationResult);

                    okButton.setEnabled(result.valid);

                    String title = result.valid ? "Verification Successful" : "Verification Failed";
                    int messageType =
                            result.valid
                                    ? JOptionPane.INFORMATION_MESSAGE
                                    : JOptionPane.ERROR_MESSAGE;
                    String formattedMessage =
                            result.message.replaceAll("(.{1,80})(\\s+|$)", "$1\n");
                    JOptionPane.showMessageDialog(null, formattedMessage, title, messageType);

                    verifyButton.setEnabled(true);
                });
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
        JButton refreshButton = new JButton(AllIcons.Actions.Refresh);
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
}
