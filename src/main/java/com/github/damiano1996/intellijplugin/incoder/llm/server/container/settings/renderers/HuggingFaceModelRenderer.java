package com.github.damiano1996.intellijplugin.incoder.llm.server.container.settings.renderers;

import com.github.damiano1996.intellijplugin.incoder.llm.server.container.settings.ContainerSettings;
import java.awt.*;
import javax.swing.*;

public class HuggingFaceModelRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof ContainerSettings.State.HuggingFace.Model model) {
            setText(model.getName());
            setToolTipText(model.getDescription());
        }
        return this;
    }
}
