package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.blocks.text;

import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.MarkdownChatMessage.MARKDOWN_CODE_BLOCK_DELIMITER;
import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.MarkdownChatMessage.MARKDOWN_CODE_BLOCK_START_REGEX;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.MarkdownChatMessage;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.blocks.MarkdownBlock;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.blocks.PatternFinder;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.blocks.code.CodeMarkdownBlock;
import com.github.damiano1996.jetbrains.incoder.ui.components.MarkdownEditorPane;
import com.intellij.util.ui.JBUI;
import javax.swing.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class TextMarkdownBlock implements MarkdownBlock {

    private final MarkdownChatMessage markdownChatMessage;
    private final MarkdownEditorPane markdownEditorPane;

    public TextMarkdownBlock(MarkdownChatMessage markdownChatMessage) {
        this(markdownChatMessage, "");
    }

    public TextMarkdownBlock(MarkdownChatMessage markdownChatMessage, String text) {
        this.markdownChatMessage = markdownChatMessage;
        markdownEditorPane = new MarkdownEditorPane();
        markdownEditorPane.setText(text);
    }

    private static @NotNull String getNextCodeLanguage(@NotNull String code) {
        return code.split("\n")[0].replace(MARKDOWN_CODE_BLOCK_DELIMITER, "").trim();
    }

    private static @NotNull String getNextCode(String code) {
        code = code.replaceFirst("^([^\n]*\n)", "");
        return code;
    }

    @Override
    public void write(String token) {
        markdownEditorPane.setText(markdownEditorPane.getText() + token);

        try {
            lookForCodeBlock();
        } catch (PatternFinder.PatternNotFound ignored) {
            log.debug("No code block has been found");
        }
    }

    private void lookForCodeBlock() throws PatternFinder.PatternNotFound {
        String markdown = markdownEditorPane.getText();

        int delimiterIndex =
                new PatternFinder().getFirstMatchIndex(MARKDOWN_CODE_BLOCK_START_REGEX, markdown);
        log.debug("Code block found at index: {}", delimiterIndex);

        var textUntilCode = markdown.substring(0, delimiterIndex);
        var nextCode = markdown.substring(delimiterIndex);

        log.debug("Creating next code block");
        String language = getNextCodeLanguage(nextCode);
        nextCode = getNextCode(nextCode);

        log.debug("Updating current block and creating the next");
        markdownEditorPane.setText(textUntilCode);
        markdownChatMessage.next(new CodeMarkdownBlock(markdownChatMessage, language, nextCode));
    }

    @Override
    public String getText() {
        return markdownEditorPane.getText();
    }

    @Override
    public JPanel getMainPanel() {
        return new JPanel() {
            {
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                setFocusable(false);
                add(markdownEditorPane);
                setBorder(JBUI.Borders.empty(0, 40, 10, 0));
            }
        };
    }
}
