package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

import javax.swing.*;

public class MarkdownPanel extends JEditorPane {

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    @Override
    public void setText(String text) {
        String html = renderer.render(parser.parse(text));

        String styledHtml =
                "<html><head><style>body { font-family: Arial, sans-serif; }</style></head><body>%s</body></html>".formatted(html);

        super.setContentType("text/html");
        super.setText(styledHtml);
    }


}
