package com.github.damiano1996.jetbrains.incoder.ui.components;

import com.intellij.ui.components.JBTextArea;
import java.awt.*;

public class RoundedJBTextArea extends JBTextArea {

    private final int arcWidth;
    private final int arcHeight;

    public RoundedJBTextArea(int arcWidth, int arcHeight) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);
        super.paintComponent(g);
    }
}
