package com.github.damiano1996.jetbrains.incoder.language.model.server.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.intellij.icons.AllIcons;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

@Slf4j
@Getter
public class LanguageModelSettingsComponent {

    private final JPanel mainPanel;
    private final JTable table;
    private final LanguageModelTableModel tableModel;
    private final JButton addButton;

    private final List<LanguageModelParameters> languageModelParameters = new ArrayList<>();

    public LanguageModelSettingsComponent() {
        tableModel = new LanguageModelTableModel(languageModelParameters);
        table = new JBTable(tableModel);
        table.getColumn("Actions").setCellRenderer(new DeleteButtonRenderer());
        table.getColumn("Actions").setCellEditor(new DeleteButtonEditor(new JCheckBox()));
        table.setRowHeight(30);

        JScrollPane scrollPane = new JBScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 200));

        addButton = new JButton("Add Language Model");
        addButton.setIcon(AllIcons.General.Add);
        addButton.addActionListener(
                e -> {
                    LanguageModelParametersDialog languageModelParametersDialog =
                            new LanguageModelParametersDialog(ProjectUtil.getActiveProject());
                    Optional<LanguageModelParameters> optionalParameters =
                            languageModelParametersDialog.getLanguageModelParameters();

                    if (optionalParameters.isPresent()) {
                        languageModelParameters.add(optionalParameters.get());
                        tableModel.fireTableDataChanged();
                    }
                });

        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponent(new JBLabel("Language model configurations:"))
                        .addComponent(addButton)
                        .addComponent(scrollPane)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();
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
