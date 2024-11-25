package com.github.damiano1996.intellijplugin.incoder.tool.window.chat;

import com.github.damiano1996.intellijplugin.incoder.tool.window.ChatMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.ChatBody;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.MessageComponent;
import com.intellij.openapi.project.Project;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenConsumer implements Consumer<String> {

    private final MessageComponent messageComponent;
    private final ChatBody chatBody;

    public TokenConsumer(Project project, ChatMessage.Author author, ChatBody chatBody) {
        this.chatBody = chatBody;
        this.messageComponent = this.chatBody.addMessage(new ChatMessage(author, ""));
        this.messageComponent.setProject(project);
    }

    @Override
    public void accept(String token) {
        log.debug("New token received: {}", token);
        messageComponent.write(token);
        // ((ChatBody.FirableListModel<?>) chatBody.getListModel()).update(0);

        chatBody.updateUI();
        chatBody.scrollToBottom();
    }
}
