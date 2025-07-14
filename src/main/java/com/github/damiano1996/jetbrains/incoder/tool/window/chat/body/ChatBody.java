package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.MessageComponent;
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

    public void addMessage(@NotNull MessageComponent messageComponent) {
        SwingUtilities.invokeLater(
                () -> {
                    messagesPanel.add(messageComponent.getMainPanel());
                    updateUIAndScroll();
                });
    }

    public void updateUI() {
        SwingUtilities.invokeLater(this::updateUIAndScroll);
    }

    private void updateUIAndScroll() {
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        boolean wasAtBottom = isScrollAtBottom(vertical);

        messagesPanel.revalidate();
        messagesPanel.repaint();

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
        return panel;
    }

    private @NotNull JPanel createWrapperPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(JBColor.namedColor("ToolWindow.background"));
        wrapper.add(messagesPanel, BorderLayout.NORTH);
        return wrapper;
    }

    private @NotNull JBScrollPane createScrollPane(JPanel wrapperPanel) {
        JBScrollPane scroll = new JBScrollPane(wrapperPanel);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        return scroll;
    }
}
