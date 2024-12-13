package com.github.damiano1996.jetbrains.incoder.ui.components;

import com.github.damiano1996.jetbrains.incoder.tool.window.ToolWindowColors;
import com.intellij.openapi.application.ApplicationManager;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarkdownEditorPane extends JEditorPane {

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    private String markdown = "";

    public MarkdownEditorPane() {
        setEditable(false);
        HTMLEditorKit editorKit = new HTMLEditorKit();
        setEditorKit(editorKit);
        setContentType("text/html");
        setOpaque(false);
        setBackground(ToolWindowColors.AI_MESSAGE_BACKGROUND);
        setForeground(ToolWindowColors.AI_MESSAGE_FOREGROUND);
        setDoubleBuffered(true);
    }

    @Override
    public String getText() {
        return markdown;
    }

    @Override
    public void setText(String text) {
        markdown = text;

        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            String html = renderer.render(parser.parse(markdown));
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
}
