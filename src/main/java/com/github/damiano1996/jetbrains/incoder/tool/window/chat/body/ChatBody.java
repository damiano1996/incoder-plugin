package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.MessageComponent;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import java.awt.*;
import javax.swing.*;
import javax.swing.Timer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ChatBody {
    private JPanel mainPanel;
    private JPanel messagesPanel;
    private JScrollPane scrollPane;
    private Timer updateTimer;
    private volatile boolean pendingUpdate = false;
    private static final int UPDATE_DELAY_MS = 50; // Batch updates every 50ms

    public ChatBody() {
        initializeUpdateTimer();
    }

    private void initializeUpdateTimer() {
        updateTimer =
                new Timer(
                        UPDATE_DELAY_MS,
                        e -> {
                            if (pendingUpdate) {
                                performUpdate();
                                pendingUpdate = false;
                            }
                        });
        updateTimer.setRepeats(true);
        updateTimer.start();
    }

    public void addMessage(@NotNull MessageComponent messageComponent) {
        SwingUtilities.invokeLater(
                () -> {
                    messagesPanel.add(messageComponent.getMainPanel());
                    scheduleUpdate();
                });
    }

    public void updateUI() {
        SwingUtilities.invokeLater(this::scheduleUpdate);
    }

    private void scheduleUpdate() {
        pendingUpdate = true;
    }

    private void performUpdate() {
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        boolean wasAtBottom = isScrollAtBottom(vertical);

        // Use more efficient update mechanism
        messagesPanel.invalidate();
        scrollPane.getViewport().invalidate();
        scrollPane.revalidate();

        if (wasAtBottom) {
            SwingUtilities.invokeLater(this::scrollToBottom);
        }
    }

    private boolean isScrollAtBottom(@NotNull JScrollBar vertical) {
        return vertical.getValue() + vertical.getVisibleAmount() >= vertical.getMaximum() - 10;
    }

    private void scrollToBottom() {
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    private void createUIComponents() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(JBColor.namedColor("ToolWindow.background"));
        mainPanel.setDoubleBuffered(true); // Enable double buffering

        messagesPanel = createMessagesPanel();
        JPanel wrapperPanel = createWrapperPanel();
        scrollPane = createScrollPane(wrapperPanel);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private @NotNull JPanel createMessagesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(JBColor.namedColor("ToolWindow.background"));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.setDoubleBuffered(true); // Enable double buffering
        return panel;
    }

    private @NotNull JPanel createWrapperPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(JBColor.namedColor("ToolWindow.background"));
        wrapper.add(messagesPanel, BorderLayout.NORTH);
        wrapper.setDoubleBuffered(true); // Enable double buffering
        return wrapper;
    }

    private @NotNull JBScrollPane createScrollPane(JPanel wrapperPanel) {
        JBScrollPane scroll = new JBScrollPane(wrapperPanel);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport()
                .setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE); // Better for smooth scrolling
        scroll.setDoubleBuffered(true); // Enable double buffering

        // Optimize viewport for better performance
        JViewport viewport = scroll.getViewport();
        viewport.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        viewport.setOpaque(true);
        viewport.setBackground(JBColor.namedColor("ToolWindow.background"));

        return scroll;
    }

    public void dispose() {
        if (updateTimer != null) {
            updateTimer.stop();
            updateTimer = null;
        }
    }
}
