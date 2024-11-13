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
public class Tools {
    private JTextField promptTextField;
    private JButton submit;
    private Chat chat;

    @Getter
    private JPanel mainPanel;
    private HumanMessage hm;
    private HumanMessage hm2;


    public void addMessageToHistory(ChatMessage message) {
        // ChatMessageService.getInstance(project).getChatMessageList().add(message);
        chat.addMessage(message);
    }

    public void setActionListeners(Project project) {
        promptTextField.addActionListener(e -> handleAction(project));
        submit.addActionListener(e -> handleAction(project));
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
        hm = new HumanMessage();
        hm.setMessage("Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World! World! Hello World! Hello World! Hello World! Hello World! Hello World! Hello World!");

        hm2 = new HumanMessage();
        hm2.setMessage("Hi");
    }
}
