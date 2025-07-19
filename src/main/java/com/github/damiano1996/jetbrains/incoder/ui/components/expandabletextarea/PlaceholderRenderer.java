package com.github.damiano1996.jetbrains.incoder.ui.components.expandabletextarea;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import java.awt.*;

/**
 * Handles rendering of placeholder text in an ExpandableTextArea. The placeholder is shown only
 * when the text area is empty.
 */
public class PlaceholderRenderer {

    private final ExpandableTextArea textArea;

    /**
     * Creates a new PlaceholderRenderer for the specified text area.
     *
     * @param textArea The ExpandableTextArea to render placeholders for
     */
    public PlaceholderRenderer(ExpandableTextArea textArea) {
        this.textArea = textArea;
    }

    /**
     * Renders the placeholder text if the text area is empty.
     *
     * @param pG The Graphics context to render with
     */
    public void renderPlaceholder(final Graphics pG) {
        String placeholder = textArea.getPlaceholder();

        if (placeholder == null || placeholder.isEmpty() || !textArea.getText().isEmpty()) {
            return;
        }

        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(JBColor.GRAY);
        g.drawString(
                placeholder,
                JBUI.scale(textArea.getLeftPadding()),
                pG.getFontMetrics().getMaxAscent() + JBUI.scale(textArea.getTopPadding()));
    }
}
