package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks;

import com.intellij.ui.JBColor;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import org.apache.commons.lang.NotImplementedException;

public class TextMarkdownBlock extends JEditorPane implements MarkdownBlock {

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    private String body = "";

    public TextMarkdownBlock() {
        setEditable(false);
        HTMLEditorKit editorKit = new HTMLEditorKit();
        setEditorKit(editorKit);
        setContentType("text/html");
        setOpaque(false);
        setBackground(new JBColor(new Color(0, 0, 0, 0), new Color(0, 0, 0, 0)));
        setForeground(JBColor.namedColor("Label.foreground"));
        setDoubleBuffered(true);
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
        body += token;
        setText(body);
    }

    @Override
    public void undoLastWrite() {
        throw new NotImplementedException();
    }

    @Override
    public String getFullText() {
        return body;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }
}
