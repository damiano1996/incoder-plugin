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
    private final HTMLDocument htmlDoc = new HTMLDocument();
    private final HTMLEditorKit editorKit = new HTMLEditorKit();

    public MarkdownPanel() {
        setEditable(false);
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
        // adjustSize();
    }

    private void adjustSize() {
        // Force the content to layout and calculate preferred size
        setSize(new Dimension(getWidth(), Short.MAX_VALUE));
        int height = getPreferredSize().height;
        setSize(new Dimension(getWidth(), height));
    }
}
