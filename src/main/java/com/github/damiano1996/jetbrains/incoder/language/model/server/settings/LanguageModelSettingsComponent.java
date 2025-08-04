package com.github.damiano1996.jetbrains.incoder.language.model.server.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelProjectService;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ServerFactoryUtils;
import com.intellij.icons.AllIcons;
import com.intellij.ide.impl.ProjectUtil;
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
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
        ComboBox<String> serverNameField =
                new ComboBox<>(
                        LanguageModelProjectService.getInstance(ProjectUtil.getActiveProject())
                                .getAvailableServerNames()
                                .toArray(new String[0]));

        ComboBox<String> modelNameField = new ComboBox<>();
        modelNameField.setEditable(true);
        modelNameField.setPreferredSize(new Dimension(300, 30));

        JTextField baseUrlField = new JTextField();
        JPasswordField apiKeyField = new JPasswordField();
        JSpinner maxTokensSpinner = new JSpinner(new SpinnerNumberModel(2048, 100, 100000, 1));
        JSpinner temperatureSpinner = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1));

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

        serverNameField.addItemListener(
                e -> {
                    if (e.getStateChange() == ItemEvent.DESELECTED) return;

                    String serverName = (String) e.getItem();
                    updateWithDefault(
                            serverName,
                            baseUrlField,
                            apiKeyField,
                            modelNameField,
                            temperatureSpinner,
                            maxTokensSpinner);
                });

        updateWithDefault(
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
                        .getPanel();

        int result =
                JOptionPane.showConfirmDialog(
                        null,
                        panel,
                        "Add Language Model",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
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

    private static void updateWithDefault(
            String serverName,
            JTextField baseUrlField,
            JPasswordField apiKeyField,
            ComboBox<String> modelNameField,
            JSpinner temperatureSpinner,
            JSpinner maxTokensSpinner) {
        try {
            LanguageModelParameters defaultParam =
                    ServerFactoryUtils.findByName(serverName).createServer().getDefaultParameters();
            baseUrlField.setText(defaultParam.getBaseUrl());
            apiKeyField.setText(defaultParam.getApiKey());
            modelNameField.setSelectedItem(defaultParam.getModelName());
            temperatureSpinner.setValue(defaultParam.getTemperature());
            maxTokensSpinner.setValue(defaultParam.getMaxTokens());

        } catch (LanguageModelException ex) {
            log.warn("Unable to load default params", ex);
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
        public Object getValueAt(int rowIndex, int columnIndex) {
            LanguageModelParameters param = data.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> param.getServerName();
                case 1 -> param.getModelName();
                case 2 -> param.getBaseUrl();
                case 3 -> param.getApiKey().isEmpty() ? "" : "****";
                case 4 -> param.getMaxTokens();
                case 5 -> param.getTemperature();
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
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 6 && "Delete".equals(aValue)) {
                data.remove(rowIndex);
                fireTableRowsDeleted(rowIndex, rowIndex);
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
    }
}
