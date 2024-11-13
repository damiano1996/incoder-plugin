package com.github.damiano1996.intellijplugin.incoder.tool.window.chat;

import com.github.damiano1996.intellijplugin.incoder.generation.CodeGenerationService;
import com.github.damiano1996.intellijplugin.incoder.generation.CodeUpdateResponse;
import com.github.damiano1996.intellijplugin.incoder.tool.window.ChatMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.ChatBody;
import com.intellij.openapi.project.Project;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.intellij.ui.JBColor;
import com.intellij.ui.RoundedLineBorder;
import com.intellij.ui.components.JBTextField;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class Chat {
    private JTextField prompt;
    private ChatBody chatBody;

    @Getter private JPanel mainPanel;

    public void addMessageToHistory(ChatMessage message) {
        // ChatMessageService.getInstance(project).getChatMessageList().add(message);
        chatBody.addMessage(message);
    }

    public Chat setActionListeners(Project project) {
        prompt.addActionListener(e -> handleAction(project));
        return this;
    }

    private void handleAction(Project project) {
        String prompt = this.prompt.getText();
        handlePrompt(project, prompt);
    }

    private void handlePrompt(Project project, @NotNull String prompt) {
        if (prompt.isEmpty()) {
            log.debug("Prompt is empty.");
        } else {
            log.debug("Prompt: {}", prompt);
            this.prompt.setText("");

            addMessageToHistory(new ChatMessage(ChatMessage.Author.USER, prompt));
            var codeUpdateResponse = new CodeUpdateResponse(prompt, prompt); // CodeGenerationService.getInstance(project).updateCode(prompt);
            addMessageToHistory(new ChatMessage(ChatMessage.Author.AI, codeUpdateResponse.notes()));
        }
    }

    private void createUIComponents() {
        prompt = new PlaceholderTextField("Enter a prompt...");
    }
}
