package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import javax.swing.*;
import java.awt.*;

public class RoundedLabel extends JLabel {

    private final int arcWidth;
    private final int arcHeight;

    private final int horizontalPadding;

    public RoundedLabel(int arcWidth, int arcHeight) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        this.horizontalPadding = 0;
    }

    public RoundedLabel(int arcWidth, int arcHeight, int horizontalPadding) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        this.horizontalPadding = horizontalPadding;
    }

    public RoundedLabel(Icon image, int arcWidth, int arcHeight) {
        super(image);
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        this.horizontalPadding = 0;
    }

    public RoundedLabel(Icon image, int arcWidth, int arcHeight, int horizontalPadding) {
        super(image);
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        this.horizontalPadding = horizontalPadding;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);
        super.paintComponent(g);
    }

    @Override
    public int getWidth() {
        return super.getWidth()+horizontalPadding;
    }
}
