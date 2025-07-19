package com.github.damiano1996.jetbrains.incoder.ui.components.expandabletextarea;

import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import lombok.Getter;
import lombok.Setter;

/**
 * A text area that automatically expands and contracts based on content. Features include
 * placeholder text, custom key bindings, and action listeners.
 */
@Setter
@Getter
public class ExpandableTextArea extends JBTextArea {

    private final String placeholder;
    private final int leftPadding;
    private final int topPadding;
    private final List<ActionListener> actionListeners = new ArrayList<>();
    private final int minRows;
    private final int rowHeight;

    private final TextAreaSizeManager sizeManager;
    private final PlaceholderRenderer placeholderRenderer;

    /**
     * Creates a new ExpandableTextArea with the specified placeholder text and padding.
     *
     * @param placeholder The placeholder text to display when the text area is empty
     * @param leftPadding The left padding for the placeholder text
     * @param topPadding The top padding for the placeholder text
     * @param minRows The minimum number of rows to display
     */
    public ExpandableTextArea(String placeholder, int leftPadding, int topPadding, int minRows) {
        super(minRows, 0);
        this.placeholder = placeholder;
        this.leftPadding = leftPadding;
        this.topPadding = topPadding;
        this.minRows = minRows;
        this.rowHeight = getFontMetrics(getFont()).getHeight();

        this.sizeManager = new TextAreaSizeManager(this);
        this.placeholderRenderer = new PlaceholderRenderer(this);

        setOpaque(true);
        setAutoscrolls(true);
        setLineWrap(true);
        setWrapStyleWord(true);

        Border textFieldBorder = UIManager.getBorder("TextField.border");
        Border paddingBorder = JBUI.Borders.empty(8);
        setBorder(new CompoundBorder(textFieldBorder, paddingBorder));

        setMargin(JBUI.insets(4));
        setFont(getFont().deriveFont((float) JBUI.scale(13)));

        new TextAreaKeyBindingHandler(this).setupKeyBindings();
        new TextAreaDocumentListener(this, sizeManager).setupDocumentListener();

        sizeManager.updateSize();
    }

    /**
     * Adds an ActionListener to this text area.
     *
     * @param listener the ActionListener to add
     */
    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    /**
     * Removes an ActionListener from this text area.
     *
     * @param listener the ActionListener to remove
     */
    public void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }

    /**
     * Notifies all registered action listeners.
     *
     * @param text The text to include in the action event
     */
    void notifyActionListeners(String text) {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, text);
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(event);
        }
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);
        placeholderRenderer.renderPlaceholder(pG);
    }
}
