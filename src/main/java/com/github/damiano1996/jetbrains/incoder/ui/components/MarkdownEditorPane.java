package com.github.damiano1996.jetbrains.incoder.ui.components;

import com.intellij.ide.plugins.newui.EmptyCaret;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.ui.JBUI;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarkdownEditorPane extends JEditorPane {

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    private String markdown = "";

    public MarkdownEditorPane() {
        HTMLEditorKit editorKit = new HTMLEditorKit();
        setEditorKit(editorKit);
        setContentType("text/html");
        setOpaque(false);
        setDoubleBuffered(true);
        setEditable(false);
        setHighlighter(null);
        setEnabled(true);
        setCaret(new EmptyCaret());
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

                            Font font = UIManager.getFont("Label.font");
                            String fontFamily = font.getFamily();
                            int fontSize = font.getSize();

                            String styledHtml =
                                    """
                                    <html><head><style>
                                    body {
                                        font-family: '%s';
                                        font-size: %dpt;
                                        background-color: transparent;
                                        padding: %dpx;
                                        margin: 0;
                                        max-width: 400px;
                                        width: 400px;
                                    }
                                    code {
                                        padding: 2px 4px;
                                        margin: 1px 2px;
                                        border-radius: 4px;
                                        font-family: '%s';
                                        font-size: %dpt;
                                    }
                                    pre {
                                        padding: %dpx;
                                        border-radius: 6px;
                                        overflow: auto;
                                        font-family: '%s';
                                        font-size: %dpt;
                                    }
                                    pre code {
                                        background-color: transparent;
                                        padding: 0;
                                        margin: 0;
                                        border: 0;
                                    }
                                    </style></head><body>%s</body></html>
                                    """
                                            .formatted(
                                                    fontFamily,
                                                    fontSize,
                                                    JBUI.scale(8),
                                                    fontFamily,
                                                    fontSize - 1,
                                                    JBUI.scale(8),
                                                    fontFamily,
                                                    fontSize - 1,
                                                    html);

                            super.setText(styledHtml);
                            revalidate();
                            repaint();
                        });
    }
}
