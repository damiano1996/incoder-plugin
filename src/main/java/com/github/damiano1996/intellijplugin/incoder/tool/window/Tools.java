package com.github.damiano1996.intellijplugin.incoder.tool.window;

import com.github.damiano1996.intellijplugin.incoder.generation.CodeGenerationService;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.Chat;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.messages.HumanMessage;
import com.intellij.openapi.project.Project;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@Slf4j
public class Tools{
    private JTextField promptTextField;
    private JButton submit;
    private Chat chat;

    @Getter
    private JPanel mainPanel;


    public void addMessageToHistory(ChatMessage message) {
        // ChatMessageService.getInstance(project).getChatMessageList().add(message);
        chat.addMessage(message);
    }

    public Tools setActionListeners(Project project) {
        promptTextField.addActionListener(e -> handleAction(project));
        submit.addActionListener(e -> handleAction(project));
        return this;
    }

    private void handleAction(Project project) {
        String prompt = promptTextField.getText();
        handlePrompt(project, prompt);
    }

    private void handlePrompt(Project project, @NotNull String prompt) {
        if (prompt.isEmpty()) {
            log.debug("Prompt is empty.");
        } else {
            log.debug("Prompt: {}", prompt);
            addMessageToHistory(new ChatMessage(ChatMessage.Author.USER, prompt));
            promptTextField.setText("");
            var codeUpdateResponse = CodeGenerationService.getInstance(project).updateCode(prompt);
            addMessageToHistory(new ChatMessage(ChatMessage.Author.AI, codeUpdateResponse.notes()));
        }
    }
}
