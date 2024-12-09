package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks;

import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.MarkdownPanel.MARKDOWN_CODE_BLOCK_DELIMITER;
import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.MarkdownPanel.MARKDOWN_CODE_BLOCK_START_REGEX;

import com.github.damiano1996.jetbrains.incoder.tool.window.ToolWindowColors;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.MarkdownPanel;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextMarkdownBlock extends JEditorPane implements MarkdownBlock {

    private final MarkdownPanel markdownPanel;

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    private String markdown = "";

    public TextMarkdownBlock(MarkdownPanel markdownPanel) {
        this(markdownPanel, "");
    }

    public TextMarkdownBlock(MarkdownPanel markdownPanel, String text) {
        this.markdownPanel = markdownPanel;
        setEditable(false);
        HTMLEditorKit editorKit = new HTMLEditorKit();
        setEditorKit(editorKit);
        setContentType("text/html");
        setOpaque(false);
        setBackground(ToolWindowColors.AI_MESSAGE_BACKGROUND);
        setForeground(ToolWindowColors.AI_MESSAGE_FOREGROUND);
        setDoubleBuffered(true);

        setText(text);
    }

    @Override
    public void setText(String text) {
        SwingUtilities.invokeLater(
                () -> {
                    String html = renderer.render(parser.parse(text));
                    String styledHtml =
                            """
                    <html><head><style>
                    body { font-family: Arial, sans-serif; background: transparent; }
                    </style></head><body>%s</body></html>
                    """
                                    .formatted(html);
                    super.setText(styledHtml);
                    revalidate();
                    repaint();
                });
    }

    @Override
    public void write(String token) {
        markdown += token;
        setText(markdown);

        lookForCodeBlock();
    }

    private void lookForCodeBlock() {
        Matcher matcher = Pattern.compile(MARKDOWN_CODE_BLOCK_START_REGEX).matcher(markdown);

        int matchStartIndex = -1;

        while (matcher.find()) {
            matchStartIndex = matcher.start();
        }

        if (matchStartIndex != -1) {
            log.debug("Code block found at index: {}", matchStartIndex);

            var markdownUntilCode = markdown.substring(0, matchStartIndex);
            var code = markdown.substring(matchStartIndex);

            log.debug("Updating current markdown");
            markdown = markdownUntilCode;
            setText(markdown);

            log.debug("Creating next code block");
            String language = code.split("\n")[0].replace(MARKDOWN_CODE_BLOCK_DELIMITER, "").trim();
            code = code.replaceFirst("^([^\n]*\n)", "");

            markdownPanel.next(new CodeMarkdownBlock(markdownPanel, language, code));
        }
    }

    @Override
    public String getFullText() {
        return markdown;
    }

    @Override
    public JComponent getMainPanel() {
        return this;
    }
}
