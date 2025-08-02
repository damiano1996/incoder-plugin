package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.examples.ExamplePromptsComponent;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.MessageComponent;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class ChatBody {
    private JPanel mainPanel;
    private JPanel messagesPanel;
    private JScrollPane scrollPane;

    @Getter @Nullable private MessageComponent currentMessage;
    private ExamplePromptsComponent examplePromptsComponent;
    private boolean hasMessages = false;
    private boolean userScrolledUp = false;

    public ChatBody(ActionListener onExamplePromptSelected) {
        createUIComponents(onExamplePromptSelected);
    }

    public void addMessage(@NotNull MessageComponent messageComponent) {
        SwingUtilities.invokeLater(
                () -> {
                    hideExamplePrompts();

                    messagesPanel.add(messageComponent.getMainPanel());
                    currentMessage = messageComponent;
                    performUpdate();
                });
    }

    private void hideExamplePrompts() {
        if (!hasMessages && examplePromptsComponent != null) {
            messagesPanel.remove(examplePromptsComponent.getMainPanel());
            hasMessages = true;
        }
    }

    public void updateUI() {
        SwingUtilities.invokeLater(this::performUpdate);
    }

    private void performUpdate() {
        messagesPanel.invalidate();
        scrollPane.getViewport().invalidate();
        scrollPane.revalidate();

        if (!userScrolledUp) {
            scrollToBottom();
        }
    }

    private void scrollToBottom() {
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        SwingUtilities.invokeLater(() -> vertical.setValue(vertical.getMaximum()));
    }

    private void createUIComponents(ActionListener onExamplePromptSelected) {
        messagesPanel = createMessagesPanel();

        if (onExamplePromptSelected != null) {
            examplePromptsComponent = new ExamplePromptsComponent(onExamplePromptSelected);
            messagesPanel.add(examplePromptsComponent.getMainPanel());
        }

        JPanel wrapperPanel = createWrapperPanel();
        scrollPane = createScrollPane(wrapperPanel);

        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponentFillVertically(scrollPane, 0)
                        .getPanel();

        mainPanel.setDoubleBuffered(true);
        mainPanel.setBorder(JBUI.Borders.empty());
        mainPanel.setMinimumSize(new Dimension(400, -1));
        mainPanel.setPreferredSize(new Dimension(400, -1));
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

        JScrollBar vertical = scroll.getVerticalScrollBar();
        vertical.addAdjustmentListener(
                e -> {
                    int value = vertical.getValue();
                    int extent = vertical.getModel().getExtent();
                    int max = vertical.getMaximum();

                    // If scrollbar is exactly at the bottom, re-enable auto-scroll
                    userScrolledUp = !(value + extent >= max);
                });

        return scroll;
    }
}
