package com.github.damiano1996.jetbrains.incoder.language.model.server.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelProjectService;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactoryUtils;
import com.intellij.icons.AllIcons;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
@Getter
public class LanguageModelSettingsComponent {

    private final JPanel mainPanel;
    private final JTable table;
    private final LanguageModelTableModel tableModel;
    private final JButton addButton;

    private final List<LanguageModelParameters> configurations = new ArrayList<>();

    public LanguageModelSettingsComponent() {
        tableModel = new LanguageModelTableModel(configurations);
        table = new JBTable(tableModel);
        table.getColumn("Actions").setCellRenderer(new DeleteButtonRenderer());
        table.getColumn("Actions").setCellEditor(new DeleteButtonEditor(new JCheckBox()));
        table.setRowHeight(30);

        JScrollPane scrollPane = new JBScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 200));

        addButton = new JButton("Add Language Model");
        addButton.setIcon(AllIcons.General.Add);
        addButton.addActionListener(e -> showAddDialog());

        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponent(new JBLabel("Language model configurations:"))
                        .addComponent(addButton)
                        .addComponent(scrollPane)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();
    }

    private void showAddDialog() {
        Project activeProject = ProjectUtil.getActiveProject();
        ComboBox<String> serverNameField =
                new ComboBox<>(
                        LanguageModelProjectService.getInstance(
                                        Objects.requireNonNull(
                                                activeProject,
                                                "Project must be defined to get available server"
                                                        + " names."))
                                .getAvailableServerNames()
                                .toArray(new String[0]));

        ComboBox<String> modelNameField = new ComboBox<>();
        modelNameField.setEditable(true);
        modelNameField.setPreferredSize(new Dimension(300, 30));

        JTextField baseUrlField = new JTextField();
        JPasswordField apiKeyField = new JPasswordField();
        JSpinner maxTokensSpinner = new JSpinner(new SpinnerNumberModel(2048, 100, 100000, 1));
        JSpinner temperatureSpinner = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1));

        JButton refreshButton = getRefreshButton(serverNameField, baseUrlField, modelNameField);
        JButton verifyButton = new JButton("Verify Connection");

        serverNameField.addItemListener(
                e -> {
                    if (e.getStateChange() == ItemEvent.DESELECTED) return;

                    String serverName = (String) e.getItem();
                    resetWithDefault(
                            serverName,
                            baseUrlField,
                            apiKeyField,
                            modelNameField,
                            temperatureSpinner,
                            maxTokensSpinner);
                });

        resetWithDefault(
                serverNameField.getItem(),
                baseUrlField,
                apiKeyField,
                modelNameField,
                temperatureSpinner,
                maxTokensSpinner);

        JPanel panel =
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
                                1,
                                false)
                        .addLabeledComponent(
                                new JBLabel("Temperature:"), temperatureSpinner, 0, false)
                        .addLabeledComponent(new JBLabel("Max tokens:"), maxTokensSpinner, 0, false)
                        .addLabeledComponent(new JBLabel("Verification:"), verifyButton, 0, false)
                        .getPanel();

        // Create custom option pane to control button states
        JOptionPane optionPane =
                new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = optionPane.createDialog(null, "Add Language Model");

        var okButton = getOkButton(optionPane);
        okButton.setEnabled(false);

        // Verification button action
        verifyButton.addActionListener(
                e -> {
                    verifyButton.setEnabled(false);

                    CompletableFuture<VerificationResult> futureVerificationResult =
                            new CompletableFuture<>();

                    LanguageModelParameters testParam =
                            new LanguageModelParameters(
                                    serverNameField.getItem(),
                                    modelNameField.getItem(),
                                    baseUrlField.getText(),
                                    new String(apiKeyField.getPassword()),
                                    (Integer) maxTokensSpinner.getValue(),
                                    (Double) temperatureSpinner.getValue());

                    verifyButton.setEnabled(false);

                    VerificationResult result =
                            getVerificationResult(
                                    testParam, activeProject, futureVerificationResult);

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

        dialog.setVisible(true);

        Object result = optionPane.getValue();
        if (result != null && result.equals(JOptionPane.OK_OPTION)) {
            LanguageModelParameters param =
                    new LanguageModelParameters(
                            serverNameField.getItem(),
                            modelNameField.getItem(),
                            baseUrlField.getText(),
                            new String(apiKeyField.getPassword()),
                            (Integer) maxTokensSpinner.getValue(),
                            (Double) temperatureSpinner.getValue());

            configurations.add(param);
            tableModel.fireTableDataChanged();
        }
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

    private static @NotNull JButton getRefreshButton(
            ComboBox<String> serverNameField,
            JTextField baseUrlField,
            ComboBox<String> modelNameField) {
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

    private static void resetWithDefault(
            String serverName,
            JTextField baseUrlField,
            JPasswordField apiKeyField,
            ComboBox<String> modelNameField,
            JSpinner temperatureSpinner,
            JSpinner maxTokensSpinner) {
        try {
            LanguageModelParameters defaultParam =
                    ServerFactoryUtils.findByName(serverName).createServer().getDefaultParameters();
            baseUrlField.setText(defaultParam.baseUrl);
            apiKeyField.setText(defaultParam.apiKey);
            modelNameField.setSelectedItem(defaultParam.modelName);
            temperatureSpinner.setValue(defaultParam.temperature);
            maxTokensSpinner.setValue(defaultParam.maxTokens);

        } catch (LanguageModelException ex) {
            log.warn("Unable to load default params", ex);
        }
    }

    private static class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() {
            setOpaque(true);
            setText("Delete");
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            return this;
        }
    }

    private static class DeleteButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        private int row;

        public DeleteButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table;
            this.row = row;
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "Delete" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                LanguageModelTableModel model = (LanguageModelTableModel) table.getModel();
                model.data.remove(row);
                model.fireTableRowsDeleted(row, row);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private static class LanguageModelTableModel extends AbstractTableModel {
        private final String[] columnNames = {
            "Server Name",
            "Model Name",
            "Base URL",
            "API Key",
            "Max Tokens",
            "Temperature",
            "Actions"
        };
        private final List<LanguageModelParameters> data;

        public LanguageModelTableModel(List<LanguageModelParameters> data) {
            this.data = data;
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public @Nullable Object getValueAt(int rowIndex, int columnIndex) {
            LanguageModelParameters param = data.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> param.serverName;
                case 1 -> param.modelName;
                case 2 -> param.baseUrl;
                case 3 -> param.apiKey.isEmpty() ? "" : "****";
                case 4 -> param.maxTokens;
                case 5 -> param.temperature;
                case 6 -> "Delete";
                default -> null;
            };
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 6;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 6 ? JButton.class : String.class;
        }
    }
}
