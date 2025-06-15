package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.MessageComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ChatBody {
    private JPanel mainPanel;
    private JPanel messagesPanel;
    private JScrollPane scrollPane;
    private boolean autoScroll = true;

    public void addMessage(@NotNull MessageComponent messageComponent) {
        SwingUtilities.invokeLater(() -> {
            var messageMainPanel = messageComponent.getMainPanel();
            
            // Check if we should auto-scroll before adding
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            boolean wasAtBottom = vertical.getValue() + vertical.getVisibleAmount() >= vertical.getMaximum() - 10;
            
            messagesPanel.add(messageMainPanel);
            
            // Revalidate and repaint in proper order
            messagesPanel.revalidate();
            
            // Only scroll if user was at bottom
            if (wasAtBottom) {
                SwingUtilities.invokeLater(this::scrollToBottom);
            }
        });
    }

    public void updateUI() {
        SwingUtilities.invokeLater(() -> {
            // Store scroll position
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            boolean wasAtBottom = vertical.getValue() + vertical.getVisibleAmount() >= vertical.getMaximum() - 10;
            
            messagesPanel.revalidate();
            messagesPanel.repaint();
            
            // Restore scroll position or scroll to bottom if user was there
            if (wasAtBottom) {
                SwingUtilities.invokeLater(this::scrollToBottom);
            }
        });
    }

    private void scrollToBottom() {
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    private void createUIComponents() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(JBColor.namedColor("ToolWindow.background"));

        // Create messages panel with proper layout
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(JBColor.namedColor("ToolWindow.background"));

        // Create a wrapper panel to push messages to top and allow proper wrapping
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(JBColor.namedColor("ToolWindow.background"));
        wrapperPanel.add(messagesPanel, BorderLayout.NORTH);

        // Create scroll pane with optimized settings
        scrollPane = new JBScrollPane(wrapperPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Optimize scrolling performance
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
}
