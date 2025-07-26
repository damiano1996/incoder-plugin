package com.github.damiano1996.jetbrains.incoder.ui.components;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;

/**
 * A JPanel that provides focus-aware border styling similar to IntelliJ's text fields. The panel
 * automatically updates its border appearance when child components gain or lose focus, providing
 * visual feedback with a blue border when focused.
 */
public class FocusAwarePanel extends JPanel {

    private boolean hasFocus = false;

    /** Creates a new FocusAwarePanel with default padding of 8px. */
    public FocusAwarePanel() {
        this(8);
    }

    /**
     * Creates a new FocusAwarePanel with the specified padding.
     *
     * @param paddingSize The padding size in pixels for the inner border
     */
    public FocusAwarePanel(int paddingSize) {
        setOpaque(true);

        setBorder(JBUI.Borders.empty(paddingSize));
    }

    /**
     * Updates the panel's focus state and refreshes the border appearance.
     *
     * @param focused true if the panel should appear focused, false otherwise
     */
    public void setFocusState(boolean focused) {
        if (this.hasFocus != focused) {
            this.hasFocus = focused;
            repaint();
        }
    }

    /**
     * Returns the current focus state of the panel.
     *
     * @return true if the panel is in focused state, false otherwise
     */
    public boolean isFocused() {
        return hasFocus;
    }

    /**
     * Adds a focus listener to the specified component that will update this panel's focus state
     * when the component gains or loses focus.
     *
     * @param component The component to monitor for focus changes
     */
    public void addFocusTrackingFor(Component component) {
        component.addFocusListener(
                new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        setFocusState(true);
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        setFocusState(false);
                    }
                });
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = JBUI.scale(10);
            int stroke = JBUI.scale(hasFocus ? 2 : 1);
            int width = getWidth();
            int height = getHeight();

            Color borderColor =
                    hasFocus ? UIManager.getColor("ProgressBar.progressColor") : JBColor.border();

            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(stroke));
            g2.drawRoundRect(stroke / 2, stroke / 2, width - stroke, height - stroke, arc, arc);
        } finally {
            g2.dispose();
        }
    }
}
