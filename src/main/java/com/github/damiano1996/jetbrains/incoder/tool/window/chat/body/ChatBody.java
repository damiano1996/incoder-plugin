package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.MessageComponent;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ChatBody {
    private JPanel mainPanel;
    private JPanel messagesPanel;
    private JScrollPane scrollPane;

    public ChatBody() {
        createUIComponents();
    }

    public void addMessage(@NotNull MessageComponent messageComponent) {
        SwingUtilities.invokeLater(
                () -> {
                    messagesPanel.add(messageComponent.getMainPanel());
                    performUpdate();
                });
    }

    public void updateUI() {
        SwingUtilities.invokeLater(this::performUpdate);
    }

    private void performUpdate() {
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        boolean wasAtBottom = isScrollAtBottom(vertical);

        messagesPanel.invalidate();
        scrollPane.getViewport().invalidate();
        scrollPane.revalidate();

        if (wasAtBottom) {
            scrollToBottom();
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
        messagesPanel = createMessagesPanel();
        JPanel wrapperPanel = createWrapperPanel();
        scrollPane = createScrollPane(wrapperPanel);

        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponentFillVertically(scrollPane, 0)
                        .getPanel();

        mainPanel.setDoubleBuffered(true);
        mainPanel.setBorder(JBUI.Borders.empty());
        mainPanel.setMinimumSize(new Dimension(300, -1));
        mainPanel.setPreferredSize(new Dimension(300, -1));
        mainPanel.setOpaque(true);
    }

    private @NotNull JPanel createMessagesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.setDoubleBuffered(true);
        panel.setBorder(JBUI.Borders.empty());
        return panel;
    }

    private @NotNull JPanel createWrapperPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(messagesPanel, BorderLayout.NORTH);
        wrapper.setDoubleBuffered(true);
        return wrapper;
    }

    private @NotNull JBScrollPane createScrollPane(JPanel wrapperPanel) {
        JBScrollPane scroll = new JBScrollPane(wrapperPanel);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        scroll.setDoubleBuffered(true);
        scroll.setBorder(JBUI.Borders.empty());

        JViewport viewport = scroll.getViewport();
        viewport.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        viewport.setOpaque(true);

        return scroll;
    }
}
