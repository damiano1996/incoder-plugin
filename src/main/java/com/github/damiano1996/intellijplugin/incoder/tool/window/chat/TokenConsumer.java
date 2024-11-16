package com.github.damiano1996.intellijplugin.incoder.tool.window.chat;

import com.github.damiano1996.intellijplugin.incoder.tool.window.ChatMessage;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.ChatBody;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.MessageComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenConsumer implements Consumer<String> {

    private final MessageComponent messageComponent;
    private final ChatBody chatBody;
    private final List<String> acceptedTokens = new ArrayList<>();

    public TokenConsumer(ChatMessage.Author author, ChatBody chatBody) {
        this.chatBody = chatBody;
        this.messageComponent = this.chatBody.addMessage(new ChatMessage(author, ""));
    }

    @Override
    public void accept(String token) {
        log.debug("New token received: {}", token);
        acceptedTokens.add(token);
        messageComponent.setMessage(String.join("", acceptedTokens));
        ((ChatBody.FirableListModel<?>)chatBody.getListModel()).update(0);
    }
}
