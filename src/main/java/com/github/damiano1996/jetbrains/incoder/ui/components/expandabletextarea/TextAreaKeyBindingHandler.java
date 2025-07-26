package com.github.damiano1996.jetbrains.incoder.ui.components.expandabletextarea;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;

/**
 * Handles key bindings for the ExpandableTextArea. Configures Enter to submit and Shift+Enter to
 * add a new line.
 */
public class TextAreaKeyBindingHandler {

    public static final String SUBMIT = "submit";
    public static final String NEWLINE = "newline";
    private final ExpandableTextArea textArea;

    /**
     * Creates a new key binding handler for the specified text area.
     *
     * @param textArea The text area to handle key bindings for
     */
    public TextAreaKeyBindingHandler(ExpandableTextArea textArea) {
        this.textArea = textArea;
    }

    /**
     * Sets up key bindings for the text area. - Enter submits the content - Shift+Enter adds a new
     * line
     */
    public void setupKeyBindings() {
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textArea.getActionMap();

        KeyStroke shiftEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

        inputMap.put(enter, SUBMIT);
        inputMap.put(shiftEnter, NEWLINE);

        actionMap.put(
                SUBMIT,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!textArea.getText().trim().isEmpty()) {
                            textArea.notifyActionListeners(textArea.getText());
                        }
                    }
                });

        actionMap.put(
                NEWLINE,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        textArea.replaceSelection("\n");
                    }
                });
    }
}
