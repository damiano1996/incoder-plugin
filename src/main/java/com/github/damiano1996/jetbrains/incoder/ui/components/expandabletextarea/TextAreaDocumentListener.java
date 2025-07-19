package com.github.damiano1996.jetbrains.incoder.ui.components.expandabletextarea;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Document listener for the ExpandableTextArea. Updates the text area size when the document
 * changes.
 */
public class TextAreaDocumentListener {

    private final ExpandableTextArea textArea;
    private final TextAreaSizeManager sizeManager;

    /**
     * Creates a new document listener for the specified text area.
     *
     * @param textArea The text area to listen to document changes for
     * @param sizeManager The size manager to update when the document changes
     */
    public TextAreaDocumentListener(ExpandableTextArea textArea, TextAreaSizeManager sizeManager) {
        this.textArea = textArea;
        this.sizeManager = sizeManager;
    }

    /** Sets up the document listener for the text area. */
    public void setupDocumentListener() {
        textArea.getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                sizeManager.updateSize();
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) {
                                sizeManager.updateSize();
                            }

                            @Override
                            public void changedUpdate(DocumentEvent e) {
                                sizeManager.updateSize();
                            }
                        });
    }
}
