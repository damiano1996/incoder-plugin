package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import com.intellij.ui.JBColor;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

public class MarkdownEditorPane extends JEditorPane implements StreamWriter {

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    private String writtenText = "";

    public MarkdownEditorPane() {
        setEditable(false);
        HTMLEditorKit editorKit = new HTMLEditorKit();
        setEditorKit(editorKit);
        setContentType("text/html");
    }

    @Override
    public void setText(String text) {
        String html = renderer.render(parser.parse(text));
        String styledHtml =
                """
                <html><head><style>
                body { font-family: Arial, sans-serif; background: transparent; }
                </style></head><body>%s</body></html>
                """
                        .formatted(html);
        super.setText(styledHtml);
    }

    @Override
    public void write(String token) {
        writtenText += token;
        setText(writtenText);
    }
}
