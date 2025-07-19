package com.github.damiano1996.jetbrains.incoder.ui.components.expandabletextarea;

import com.intellij.util.ui.JBUI;
import java.awt.*;
import javax.swing.*;

/**
 * Manages the size and resizing behavior of an ExpandableTextArea. Handles calculating the
 * appropriate size based on content and constraints.
 */
public class TextAreaSizeManager {

    private final ExpandableTextArea textArea;

    /**
     * Creates a new TextAreaSizeManager for the specified text area.
     *
     * @param textArea The ExpandableTextArea to manage
     */
    public TextAreaSizeManager(ExpandableTextArea textArea) {
        this.textArea = textArea;
    }

    /**
     * Updates the size of the text area based on its content. Ensures the text area respects
     * minimum and maximum row constraints.
     */
    public void updateSize() {
        try {
            FontMetrics fm = textArea.getFontMetrics(textArea.getFont());

            int lineCount = getWrappedLineCount();
            lineCount++;

            int rows = Math.max(textArea.getMinRows(), lineCount);

            int lineHeight = fm.getHeight();
            int textHeight = rows * lineHeight;
            int paddingHeight = JBUI.scale(8);
            int newHeight = textHeight + paddingHeight;

            int width = textArea.getWidth();
            if (width <= 0) {
                width = JBUI.scale(200);
            }

            Dimension newSize = new Dimension(width, newHeight);

            textArea.setPreferredSize(newSize);
            textArea.setMinimumSize(
                    new Dimension(width, textArea.getMinRows() * lineHeight + paddingHeight));

            Container parent = textArea.getParent();
            if (parent instanceof JViewport) {
                textArea.setSize(newSize);
            }

            textArea.revalidate();
            textArea.repaint();

            if (parent != null) {
                parent.invalidate();
                parent.validate();

                Container grandparent = parent.getParent();
                if (grandparent != null) {
                    grandparent.invalidate();
                    grandparent.validate();
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating text area size: " + e.getMessage());
        }
    }

    /**
     * Calculates the number of visual lines including wrapped lines. This method accounts for text
     * wrapping based on the component width.
     *
     * @return The total number of visual lines including wrapped lines
     */
    private int getWrappedLineCount() {
        try {
            if (textArea.getText().isEmpty()) {
                return 1;
            }

            int availableWidth =
                    textArea.getWidth() - textArea.getInsets().left - textArea.getInsets().right;
            if (availableWidth <= 0) {
                availableWidth =
                        JBUI.scale(200) - textArea.getInsets().left - textArea.getInsets().right;
            }

            FontMetrics fm = textArea.getFontMetrics(textArea.getFont());

            int totalLines = 0;
            String[] lines = textArea.getText().split("\n", -1);

            for (String line : lines) {
                if (line.isEmpty()) {
                    totalLines++;
                } else {
                    int lineWidth = fm.stringWidth(line);
                    int wrappedLines =
                            Math.max(1, (int) Math.ceil((double) lineWidth / availableWidth));
                    totalLines += wrappedLines;
                }
            }

            return totalLines;
        } catch (Exception e) {
            return textArea.getLineCount();
        }
    }
}
