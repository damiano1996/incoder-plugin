package com.github.damiano1996.jetbrains.incoder.ui.components;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * A JPanel that provides focus-aware border styling similar to IntelliJ's text fields. The panel
 * automatically updates its border appearance when child components gain or lose focus, providing
 * visual feedback with a blue border when focused.
 */
public class FocusAwarePanel extends JPanel {

    private final Border focusedBorder;
    private final Border unfocusedBorder;

    public FocusAwarePanel(Border focusedBorder, Border unfocusedBorder) {
        this.focusedBorder = focusedBorder;
        this.unfocusedBorder = unfocusedBorder;

        setBorder(unfocusedBorder);
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
                        setBorder(focusedBorder);
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        setBorder(unfocusedBorder);
                    }
                });
    }
}
