package com.github.damiano1996.jetbrains.incoder.ui.components;

import java.awt.*;
import javax.swing.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RoundedUtils {

    public Graphics2D getRoundedComponentGraphics(
            JComponent component, Graphics graphics, int arcDimension) {
        Graphics2D newGraphics = (Graphics2D) graphics;
        newGraphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        newGraphics.setColor(component.getBackground());
        newGraphics.fillRoundRect(
                0, 0, component.getWidth(), component.getHeight(), arcDimension, arcDimension);
        return newGraphics;
    }
}
