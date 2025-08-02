package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.ChatBody;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ChatMessage;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.blocks.MarkdownBlock;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.blocks.text.TextMarkdownBlock;
import javax.swing.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarkdownChatMessage implements ChatMessage {

    public static final String MARKDOWN_CODE_BLOCK_DELIMITER = "```";
    public static final String MARKDOWN_CODE_BLOCK_START_REGEX = "(?m)^```(?:[a-zA-Z0-9]+)?\\n";
    public static final String MARKDOWN_CODE_BLOCK_END_REGEX = "(?m)^```";

    private final ChatBody chatBody;
    private MarkdownBlock currentMarkdownBlock;

    public MarkdownChatMessage(ChatBody chatBody) {
        this.chatBody = chatBody;

        log.debug("Start with a markdown editor pane");
        next(new TextMarkdownBlock(this));
    }

    public void next(MarkdownBlock markdownBlock) {
        currentMarkdownBlock = markdownBlock;
        chatBody.addChatMessage(markdownBlock);
    }

    @Override
    public void write(String token) {
        currentMarkdownBlock.write(token);
    }

    @Override
    public String getText() {
        return currentMarkdownBlock.getText();
    }

    @Override
    public void closeStream() {
        currentMarkdownBlock.closeStream();
    }

    @Override
    public JPanel getMainPanel() {
        return currentMarkdownBlock.getMainPanel();
    }
}
