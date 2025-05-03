package com.github.damiano1996.jetbrains.incoder.ui.components;

import com.intellij.util.ui.UIUtil;
import java.awt.*;
import javax.swing.*;

public class DescriptionLabel extends JLabel {

    public DescriptionLabel(String text) {
        super(text);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setForeground(UIUtil.getLabelDisabledForeground());
        Font font = getFont();
        int size = font.getSize();
        if (size >= 12) {
            size -= 2;
        }
        setFont(font.deriveFont((float) size));
    }
}
