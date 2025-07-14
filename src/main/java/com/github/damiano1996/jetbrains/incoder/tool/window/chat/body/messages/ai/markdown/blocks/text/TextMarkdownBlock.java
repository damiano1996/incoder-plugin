package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.text;

import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.MarkdownPanel.MARKDOWN_CODE_BLOCK_DELIMITER;
import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.MarkdownPanel.MARKDOWN_CODE_BLOCK_START_REGEX;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.MarkdownPanel;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.MarkdownBlock;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.PatternFinder;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.code.CodeMarkdownBlock;
import com.github.damiano1996.jetbrains.incoder.ui.components.MarkdownEditorPane;
import javax.swing.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class TextMarkdownBlock implements MarkdownBlock {

    private final MarkdownPanel markdownPanel;
    private final MarkdownEditorPane markdownEditorPane;

    public TextMarkdownBlock(MarkdownPanel markdownPanel) {
        this(markdownPanel, "");
    }

    public TextMarkdownBlock(MarkdownPanel markdownPanel, String text) {
        this.markdownPanel = markdownPanel;
        markdownEditorPane = new MarkdownEditorPane();
        markdownEditorPane.setText(text);
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
        markdownPanel.next(new CodeMarkdownBlock(markdownPanel, language, nextCode));
    }

    private static @NotNull String getNextCodeLanguage(@NotNull String code) {
        return code.split("\n")[0].replace(MARKDOWN_CODE_BLOCK_DELIMITER, "").trim();
    }

    private static @NotNull String getNextCode(String code) {
        code = code.replaceFirst("^([^\n]*\n)", "");
        return code;
    }

    @Override
    public String getText() {
        return markdownEditorPane.getText();
    }

    @Override
    public void streamClosed() {}

    @Override
    public JComponent getMainPanel() {
        return markdownEditorPane;
    }
}
