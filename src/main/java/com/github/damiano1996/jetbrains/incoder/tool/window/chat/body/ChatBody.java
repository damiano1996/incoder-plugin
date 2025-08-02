package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.examples.ExamplePromptsComponent;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ChatMessage;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.StreamWriter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Chat body component that manages the display of messages and example prompts with automatic
 * scrolling and dynamic content management. Thread-safe implementation with proper EDT handling.
 */
@Slf4j
public class ChatBody implements StreamWriter {

    private static final int SCROLL_THRESHOLD = 50;

    @Getter private final JPanel mainPanel;
    private final JPanel messagesPanel;
    private final JBScrollPane scrollPane;
    private final JScrollBar verticalScrollBar;

    // Thread-safe collections and flags
    private final List<ChatMessage> messages = Collections.synchronizedList(new ArrayList<>());
    private final AtomicBoolean userScrolledUp = new AtomicBoolean(false);
    private final AtomicBoolean programmaticScroll = new AtomicBoolean(false);

    @Nullable private ExamplePromptsComponent examplePromptsComponent;

    public ChatBody(@Nullable ActionListener onExamplePromptSelected) {
        this.messagesPanel = createMessagesPanel();
        this.scrollPane = createScrollPane();
        this.verticalScrollBar = scrollPane.getVerticalScrollBar();
        this.mainPanel = createMainPanel();

        initializeExamplePrompts(onExamplePromptSelected);
        setupScrollListener();
    }

    /** Returns an unmodifiable view of the messages list for safe external access. */
    public List<ChatMessage> getMessages() {
        return Collections.unmodifiableList(new ArrayList<>(messages));
    }

    /**
     * Adds a new message to the chat and updates the UI. Thread-safe method that ensures UI updates
     * happen on EDT.
     */
    public void addChatMessage(@NotNull ChatMessage chatMessage) {
        // Ensure UI updates happen on EDT
        SwingUtilities.invokeLater(() -> doAddMessage(chatMessage));
    }

    private void doAddMessage(@NotNull ChatMessage chatMessage) {
        hideExamplePromptsIfNeeded();

        messages.add(chatMessage);
        messagesPanel.add(chatMessage.getMainPanel());

        refreshUI();
        scrollToBottomIfNeeded();
    }

    private void refreshUI() {
        messagesPanel.revalidate();
        messagesPanel.repaint();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void scrollToBottomIfNeeded() {
        if (!userScrolledUp.get()) {
            scrollToBottomInternal();
        }
    }

    private void scrollToBottomInternal() {
        SwingUtilities.invokeLater(
                () -> {
                    programmaticScroll.set(true);
                    verticalScrollBar.setValue(verticalScrollBar.getMaximum());
                });
    }

    private void hideExamplePromptsIfNeeded() {
        if (messages.isEmpty() && examplePromptsComponent != null) {
            messagesPanel.remove(examplePromptsComponent.getMainPanel());
        }
    }

    private void showExamplePromptsIfNeeded() {
        if (messages.isEmpty() && examplePromptsComponent != null) {
            messagesPanel.add(examplePromptsComponent.getMainPanel(), 0);
        }
    }

    private void initializeExamplePrompts(@Nullable ActionListener onExamplePromptSelected) {
        if (onExamplePromptSelected != null) {
            examplePromptsComponent = new ExamplePromptsComponent(onExamplePromptSelected);
            messagesPanel.add(examplePromptsComponent.getMainPanel());
        }
    }

    private void setupScrollListener() {
        verticalScrollBar.addAdjustmentListener(
                e -> {
                    if (programmaticScroll.compareAndSet(true, false)) {
                        return;
                    }

                    int value = verticalScrollBar.getValue();
                    int extent = verticalScrollBar.getModel().getExtent();
                    int maximum = verticalScrollBar.getMaximum();

                    boolean isNearBottom = (value + extent + SCROLL_THRESHOLD) >= maximum;
                    userScrolledUp.set(!isNearBottom);

                    if (log.isDebugEnabled()) {
                        log.debug(
                                "Scroll position: value={}, extent={}, max={}, userScrolledUp={}",
                                value,
                                extent,
                                maximum,
                                userScrolledUp.get());
                    }
                });
    }

    @NotNull
    private JPanel createMessagesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setOpaque(false);
        panel.setBorder(JBUI.Borders.empty(8));
        return panel;
    }

    @NotNull
    private JBScrollPane createScrollPane() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(messagesPanel, BorderLayout.NORTH);
        wrapperPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);
        wrapperPanel.setOpaque(false);

        JBScrollPane scroll = new JBScrollPane(wrapperPanel);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(JBUI.Borders.empty());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    @NotNull
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setBorder(JBUI.Borders.empty());
        panel.setOpaque(false);
        return panel;
    }

    @Override
    public void write(@NotNull String token) {
        SwingUtilities.invokeLater(
                () -> {
                    synchronized (messages) {
                        if (messages.isEmpty()) {
                            log.warn("No messages available to write token to");
                            return;
                        }

                        try {
                            ChatMessage lastMessage = messages.get(messages.size() - 1);
                            lastMessage.write(token);
                            // Only refresh UI periodically to avoid performance issues
                            refreshUIThrottled();
                        } catch (Exception e) {
                            log.error("Error writing token to message", e);
                        }
                    }
                });
    }

    @Override
    public String getText() {
        synchronized (messages) {
            if (messages.isEmpty()) {
                return "";
            }

            try {
                return messages.get(messages.size() - 1).getText();
            } catch (Exception e) {
                log.error("Error getting text from last message", e);
                return "";
            }
        }
    }

    @Override
    public void closeStream() {
        SwingUtilities.invokeLater(
                () -> {
                    synchronized (messages) {
                        if (messages.isEmpty()) {
                            log.warn("No messages available to close stream on");
                            return;
                        }

                        try {
                            ChatMessage lastMessage = messages.get(messages.size() - 1);
                            lastMessage.closeStream();
                            refreshUI();
                            scrollToBottomIfNeeded();
                        } catch (Exception e) {
                            log.error("Error closing stream on message", e);
                        }
                    }
                });
    }

    // Throttled UI refresh to prevent excessive repaints during streaming
    private volatile long lastRefreshTime = 0;
    private static final long REFRESH_THROTTLE_MS = 50; // Max 20 FPS

    private void refreshUIThrottled() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRefreshTime > REFRESH_THROTTLE_MS) {
            lastRefreshTime = currentTime;
            refreshUI();
            scrollToBottomIfNeeded();
        }
    }

    /** Checks if the chat is empty (no messages). */
    public boolean isEmpty() {
        return messages.isEmpty();
    }
}
