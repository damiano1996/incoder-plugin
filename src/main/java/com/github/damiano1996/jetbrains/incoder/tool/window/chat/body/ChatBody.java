package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.examples.ExamplePromptsComponent;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ChatMessage;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.StreamWriter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.EmptyStackException;
import java.util.Stack;
import javax.swing.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Chat body component that manages the display of messages and example prompts with automatic
 * scrolling and dynamic content management.
 */
@Slf4j
public class ChatBody implements StreamWriter {

    private static final int SCROLL_THRESHOLD = 50;

    @Getter private final JPanel mainPanel;
    private final JPanel messagesPanel;
    private final JBScrollPane scrollPane;

    @Getter private final Stack<ChatMessage> messages;

    @Nullable private ExamplePromptsComponent examplePromptsComponent;

    private volatile boolean userScrolledUp = false;
    private volatile boolean programmaticScroll = false;

    public ChatBody(@Nullable ActionListener onExamplePromptSelected) {
        this.messages = new Stack<>();
        this.messagesPanel = createMessagesPanel();
        this.scrollPane = createScrollPane();
        this.mainPanel = createMainPanel();

        initializeExamplePrompts(onExamplePromptSelected);
        setupScrollListener();
    }

    /**
     * Adds a new message to the chat and updates the UI. Thread-safe method that ensures UI updates
     * happen on EDT.
     */
    public void addChatMessage(ChatMessage chatMessage) {
        if (SwingUtilities.isEventDispatchThread()) {
            doAddMessage(chatMessage);
        } else {
            SwingUtilities.invokeLater(() -> doAddMessage(chatMessage));
        }
    }

    private void doAddMessage(ChatMessage messageComponent) {
        hideExamplePromptsIfNeeded();

        messages.add(messageComponent);
        messagesPanel.add(messageComponent.getMainPanel());

        scrollToBottom();

        updateUI();
    }

    /** Forces a UI refresh and scrolls to bottom if user hasn't scrolled up. */
    private void updateUI() {
        if (SwingUtilities.isEventDispatchThread()) {
            performUpdate();
        } else {
            SwingUtilities.invokeLater(this::performUpdate);
        }
    }

    /** Scrolls to the bottom of the chat, regardless of current scroll position. */
    public void scrollToBottom() {
        SwingUtilities.invokeLater(
                () -> {
                    programmaticScroll = true;
                    JScrollBar vertical = scrollPane.getVerticalScrollBar();
                    vertical.setValue(vertical.getMaximum());
                    userScrolledUp = false;
                });
    }

    private void hideExamplePromptsIfNeeded() {
        if (messages.isEmpty() && examplePromptsComponent != null) {
            messagesPanel.remove(examplePromptsComponent.getMainPanel());
        }
    }

    private void performUpdate() {
        if (!userScrolledUp) {
            scrollToBottomInternal();
        }
    }

    private void scrollToBottomInternal() {
        SwingUtilities.invokeLater(
                () -> {
                    programmaticScroll = true;
                    JScrollBar vertical = scrollPane.getVerticalScrollBar();
                    vertical.setValue(vertical.getMaximum());
                });
    }

    private void initializeExamplePrompts(@Nullable ActionListener onExamplePromptSelected) {
        if (onExamplePromptSelected != null) {
            examplePromptsComponent = new ExamplePromptsComponent(onExamplePromptSelected);
            messagesPanel.add(examplePromptsComponent.getMainPanel());
        }
    }

    private void setupScrollListener() {
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.addAdjustmentListener(
                e -> {
                    if (programmaticScroll) {
                        programmaticScroll = false;
                        return;
                    }

                    int value = vertical.getValue();
                    int extent = vertical.getModel().getExtent();
                    int max = vertical.getMaximum();

                    userScrolledUp = (value + extent + SCROLL_THRESHOLD) < max;
                });
    }

    private @NotNull JPanel createMessagesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setOpaque(false);
        panel.setBorder(JBUI.Borders.empty(8));
        return panel;
    }

    private @NotNull JBScrollPane createScrollPane() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(messagesPanel, BorderLayout.NORTH);
        wrapperPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);
        wrapperPanel.setOpaque(false);

        JBScrollPane scroll = new JBScrollPane(wrapperPanel);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(JBUI.Borders.empty());

        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setBlockIncrement(64);

        return scroll;
    }

    private @NotNull JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setBorder(JBUI.Borders.empty());
        panel.setOpaque(false);
        return panel;
    }

    @Override
    public void write(String token) {
        try {
            messages.peek().write(token);
            updateUI();
        } catch (EmptyStackException e) {
            log.warn("Unable to write token", e);
        }
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public void closeStream() {
        try {
            messages.peek().closeStream();
            updateUI();
        } catch (EmptyStackException e) {
            log.warn("Unable to close stream", e);
        }
    }
}
