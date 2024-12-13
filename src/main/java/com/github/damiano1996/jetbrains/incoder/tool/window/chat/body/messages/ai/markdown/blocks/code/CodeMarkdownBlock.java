package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.code;

import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.MarkdownPanel.*;

import com.github.damiano1996.jetbrains.incoder.tool.window.ToolWindowColors;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.MarkdownPanel;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.MarkdownBlock;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.PatternFinder;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.code.toolbar.CodeActionToolbar;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.text.TextMarkdownBlock;
import com.github.damiano1996.jetbrains.incoder.ui.components.EditorPanel;
import com.intellij.lang.Language;
import com.intellij.openapi.Disposable;
import javax.swing.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class CodeMarkdownBlock implements MarkdownBlock, Disposable {

    @Getter(AccessLevel.PROTECTED)
    private final MarkdownPanel markdownPanel;

    @Getter(AccessLevel.PROTECTED)
    private final EditorPanel editorPanel;

    public CodeMarkdownBlock(
            @NotNull MarkdownPanel markdownPanel, String language, @NotNull String text) {
        this.markdownPanel = markdownPanel;
        editorPanel =
                new EditorPanel(markdownPanel.getProject(), EditorPanel.guessLanguage(language));
        editorPanel.setText(text);
    }

    @Override
    public JComponent getMainPanel() {
        var toolbar = CodeActionToolbar.createActionToolbarComponent(editorPanel, this);

        return new JPanel() {
            {
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                setBackground(ToolWindowColors.AI_MESSAGE_BACKGROUND);
                setForeground(ToolWindowColors.AI_MESSAGE_FOREGROUND);
                setFocusable(false);

                add(editorPanel);
                add(toolbar);
            }
        };
    }

    @Override
    public void write(@NotNull String token) {
        editorPanel.setText(editorPanel.getText() + token);

        try {
            lookForTextBlock(editorPanel.getText());
        } catch (PatternFinder.PatternNotFound ignored) {
            log.debug("No text block has been found");
        }
    }

    private void lookForTextBlock(String code) throws PatternFinder.PatternNotFound {
        if (editorPanel.getLanguage().getID().equals("Markdown")) {
            lookForNextTextBlockWhenMarkdown(code);
        } else {
            lookForNextTextBlock(code);
        }
    }

    private void lookForNextTextBlockWhenMarkdown(String code)
            throws PatternFinder.PatternNotFound {
        int delimiterIndex =
                new PatternFinder().getLastMatchIndex(MARKDOWN_CODE_BLOCK_START_REGEX, code);
        log.debug("Code block found at index: {}", delimiterIndex);

        var markdownUntilCode = code.substring(0, delimiterIndex);
        var nextCode = code.substring(delimiterIndex);

        log.debug("Creating next code block");
        String language = getNextCodeLanguage(nextCode);

        if (!language.equalsIgnoreCase("Markdown")) return;

        delimiterIndex =
                new PatternFinder()
                        .getLastMatchIndex(MARKDOWN_CODE_BLOCK_END_REGEX, markdownUntilCode);

        markdownUntilCode = markdownUntilCode.substring(0, delimiterIndex);
        var nextText = getNextText(markdownUntilCode.substring(delimiterIndex));

        editorPanel.setText(markdownUntilCode);
        markdownPanel.next(new TextMarkdownBlock(markdownPanel, nextText + nextCode));
    }

    private void lookForNextTextBlock(String code) throws PatternFinder.PatternNotFound {
        int delimiterIndex =
                new PatternFinder().getFistMatchIndex(MARKDOWN_CODE_BLOCK_END_REGEX, code);

        log.debug("Text block found at index: {}", delimiterIndex);

        var codeUntilText = code.substring(0, delimiterIndex);
        var text = getNextText(code.substring(delimiterIndex));

        editorPanel.setText(codeUntilText);
        markdownPanel.next(new TextMarkdownBlock(markdownPanel, text));
    }

    private static @NotNull String getNextCodeLanguage(@NotNull String code) {
        return code.split("\n")[0].replace(MARKDOWN_CODE_BLOCK_DELIMITER, "").trim();
    }

    private static @NotNull String getNextText(String code) {
        code = code.replaceFirst("^([^\n]*\n)", "");
        return code;
    }

    @Override
    public String getText() {
        return editorPanel.getText();
    }

    @Override
    public void streamClosed() {
        var code = editorPanel.getText();

        int delimiterIndex;
        try {
            delimiterIndex =
                    new PatternFinder().getLastMatchIndex(MARKDOWN_CODE_BLOCK_END_REGEX, code);
        } catch (PatternFinder.PatternNotFound e) {
            return;
        }

        var codeUntilText = code.substring(0, delimiterIndex);
        var nextText = getNextText(code.substring(delimiterIndex));

        editorPanel.setText(codeUntilText);
        markdownPanel.next(new TextMarkdownBlock(markdownPanel, nextText));
    }

    @Override
    public void dispose() {
        editorPanel.dispose();
    }

    public Language getLanguage() {
        return editorPanel.getLanguage();
    }
}
