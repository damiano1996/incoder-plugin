package com.github.damiano1996.intellijplugin.incoder.tool.window;

import com.github.damiano1996.intellijplugin.incoder.generation.CodeGenerationService;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import static com.intellij.ide.IdeTooltipManager.setBorder;

@Slf4j
public final class InCoderToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        PromptToolWindowContent toolWindowContent =
                new PromptToolWindowContent(project, toolWindow);
        Content content =
                ContentFactory.getInstance()
                        .createContent(toolWindowContent.getTools().getMainPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color borderColor;

        public RoundedBorder(int radius, Color borderColor) {
            this.radius = radius;
            this.borderColor = borderColor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            //g2.setBackground(borderColor);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return JBUI.insets(4, 5);
        }
    }

    private static class PromptToolWindowContent {

        private final Project project;

        @Getter private final JPanel contentPanel;
//        private final JBTextField promptTextField;
//        private final JButton submitButton = new JButton("Submit");
////        private final JBList<ChatMessage> chatHistoryList;
////        private final DefaultListModel<ChatMessage> chatHistoryModel;

        @Getter
        private final Tools tools = new Tools();


        public PromptToolWindowContent(Project project, ToolWindow toolWindow) {
            this.project = project;

//            // Initialize and customize the promptTextField
//            promptTextField = new JBTextField();
//            promptTextField.setFont(new Font(promptTextField.getFont().getName(), Font.PLAIN, 14));
//            promptTextField.setBorder(new RoundedBorder(10, JBColor.GRAY));
//
//            // Add padding inside the text field
//            Insets insets = JBUI.insets(8, 10);
//            promptTextField.setMargin(insets);
//
//            chat = new Chat();

            // Initialize chat history list
//            this.chatHistoryModel = new DefaultListModel<>();
//            chatHistoryList = new JBList<>(chatHistoryModel);
//            chatHistoryList.setCellRenderer(new ChatMessageRenderer());
            //JBScrollPane scrollPane = new JBScrollPane(chatHistoryList);
//            scrollPane.setPreferredSize(new Dimension(-1, 200));
//            scrollPane.setBorder(JBUI.Borders.emptyTop(10));

//            addMessageToHistory(new ChatMessage(ChatMessage.Author.USER, "Hi assistant"));
//            addMessageToHistory(new ChatMessage(ChatMessage.Author.AI, "Hello World!"));
//
//            promptTextField.addActionListener(this::handleAction);
//            submitButton.addActionListener(this::handleAction);


            contentPanel =
                    FormBuilder.createFormBuilder()
                            .setVerticalGap(5)
                            .setFormLeftIndent(20)
//                            .addComponent(new Chat().getMainPanel())
//                            .addLabeledComponent(
//                                    new JBLabel("Enter prompt:"),
//                                    new JPanel(new BorderLayout()) {
//                                        {
//                                            setBorder(JBUI.Borders.emptyRight(10));
//                                            add(promptTextField);
//                                        }
//                                    },
//                                    1,
//                                    true)
//                            .addComponent(
//                                    new JPanel(new BorderLayout()) {
//                                        {
//                                            setBorder(JBUI.Borders.emptyRight(10));
//                                            add(submitButton, BorderLayout.EAST);
//                                        }
//                                    })
                            // .addComponent(chat.getMainPanel())
                            .addComponent(tools.getMainPanel())
                            .setFormLeftIndent(0)
                            .addComponentFillVertically(new JPanel(), 0)
                            .getPanel();

            tools.setActionListeners(project);

            tools.addMessageToHistory(new ChatMessage(ChatMessage.Author.USER, "Hi assistant"));
            tools.addMessageToHistory(new ChatMessage(ChatMessage.Author.AI, "Hello World!"));
            tools.addMessageToHistory(new ChatMessage(ChatMessage.Author.USER, "Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant Hi assistant"));
            tools.addMessageToHistory(new ChatMessage(ChatMessage.Author.AI, "Hello World!"));

            // Add the scroll pane with chat history to the bottom
            //contentPanel.setLayout(new BorderLayout());
            //contentPanel.add(scrollPane, BorderLayout.SOUTH);
        }

//        private void handleAction(ActionEvent e) {
//            String prompt = promptTextField.getText();
//            handlePrompt(prompt);
//        }
//
//        private void handlePrompt(@NotNull String prompt) {
//            if (prompt.isEmpty()) {
//                log.debug("Prompt is empty.");
//            } else {
//                log.debug("Prompt: {}", prompt);
//                addMessageToHistory(new ChatMessage(ChatMessage.Author.USER, prompt));
//                promptTextField.setText("");
//                var codeUpdateResponse = CodeGenerationService.getInstance(project).updateCode(prompt);
//                addMessageToHistory(new ChatMessage(ChatMessage.Author.AI, codeUpdateResponse.notes()));
//            }
//        }
//
//        private void addMessageToHistory(ChatMessage message) {
//            ChatMessageService.getInstance(project).getChatMessageList().add(message);
//            chat.addMessage(message);
//        }
    }


}