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
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
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

    private final List<LanguageModelParameters> languageModelParameters = new ArrayList<>();

    public LanguageModelSettingsComponent() {
        tableModel = new LanguageModelTableModel(languageModelParameters);
        table = new JBTable(tableModel);
        table.setRowHeight(30);
        table.getColumn("Actions").setCellRenderer(new ActionsRenderer());
        table.getColumn("Actions").setCellEditor(new ActionsEditor(tableModel));

        JScrollPane scrollPane = new JBScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 200));

        addButton = new JButton("Add Language Model");
        addButton.setIcon(AllIcons.General.Add);
        addButton.addActionListener(
                e -> {
                    Optional<LanguageModelParameters> optionalParameters =
                            LanguageModelParametersUtils.create(ProjectUtil.getActiveProject());

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

    private static class ActionsRenderer extends JPanel implements TableCellRenderer {

        public ActionsRenderer() {
            JButton edit = createButton("Edit", AllIcons.Actions.Edit);
            JButton delete = createButton("Delete", AllIcons.Actions.DeleteTag);

            JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
            toolbar.setFloatable(false);
            toolbar.setBorderPainted(false);
            toolbar.setOpaque(false);
            toolbar.add(edit);
            toolbar.add(delete);

            setLayout(new BorderLayout());
            add(toolbar);
            setOpaque(true);
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

    private static @NotNull JButton createButton(String txt, @NotNull Icon icon) {
        JButton edit = new JButton(txt);
        edit.setIcon(icon);
        edit.setBorderPainted(false);
        edit.setContentAreaFilled(false);
        edit.setHorizontalAlignment(SwingConstants.CENTER);
        return edit;
    }

    private static class ActionsEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new BorderLayout());
        private int row = -1;

        public ActionsEditor(LanguageModelTableModel model) {
            JButton edit = createButton("Edit", AllIcons.Actions.Edit);
            JButton delete = createButton("Delete", AllIcons.Actions.DeleteTag);

            JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
            toolbar.setFloatable(false);
            toolbar.setBorderPainted(false);
            toolbar.setOpaque(false);
            toolbar.add(edit);
            toolbar.add(delete);

            panel.add(toolbar);

            edit.addActionListener(
                    e -> {
                        Optional<LanguageModelParameters> optional =
                                LanguageModelParametersUtils.edit(
                                        ProjectUtil.getActiveProject(), model.data.get(row));
                        if (optional.isPresent() && row >= 0 && row < model.data.size()) {
                            model.data.set(row, optional.get());
                            model.fireTableRowsUpdated(row, row);
                        }
                        stopCellEditing();
                    });

            delete.addActionListener(
                    e -> {
                        if (row >= 0 && row < model.data.size()) {
                            model.data.remove(row);
                            model.fireTableRowsDeleted(row, row);
                        }
                        stopCellEditing();
                    });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    private static class LanguageModelTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Server", "Model", "Actions"};
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
            if (param == null) return null;
            return switch (columnIndex) {
                case 0 -> param.serverName;
                case 1 -> param.modelName;
                default -> null;
            };
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 2;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 2 ? Object.class : String.class;
        }
    }
}
