package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class MarkdownPanel extends JEditorPane {

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public MarkdownPanel() {
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
                <html><head><style>body { font-family: Arial, sans-serif; }</style></head><body>%s</body></html>
                """
                        .formatted(html);
        super.setText(styledHtml);
    }
}
