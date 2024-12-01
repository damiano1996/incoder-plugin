package com.github.damiano1996.intellijplugin.incoder.ui.components;

import java.awt.*;
import javax.swing.*;

public class RoundedLabel extends JLabel {

    private final int arcWidth;
    private final int arcHeight;

    private final int horizontalPadding;
    private final int verticalPadding;

    public RoundedLabel(int arcWidth, int arcHeight) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        this.horizontalPadding = 0;
        this.verticalPadding = 0;
    }

    public RoundedLabel(int arcWidth, int arcHeight, int horizontalPadding, int verticalPadding) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        this.horizontalPadding = horizontalPadding;
        this.verticalPadding = verticalPadding;
    }

    public RoundedLabel(Icon image, int arcWidth, int arcHeight) {
        super(image);
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        this.horizontalPadding = 0;
        this.verticalPadding = 0;
    }

    public RoundedLabel(
            Icon image, int arcWidth, int arcHeight, int horizontalPadding, int verticalPadding) {
        super(image);
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        this.horizontalPadding = horizontalPadding;
        this.verticalPadding = verticalPadding;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);
        super.paintComponent(g);
    }

    @Override
    public int getWidth() {
        return super.getWidth() + horizontalPadding;
    }

    @Override
    public int getHeight() {
        return super.getHeight() + verticalPadding;
    }
}
