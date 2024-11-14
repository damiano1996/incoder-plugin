package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import javax.swing.*;
import lombok.Getter;

@Getter
public class AiMessage implements MessageComponent {
    private JPanel mainPanel;
    private JEditorPane message;

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    @Override
    public MessageComponent setMessage(String markdown) {
        SwingUtilities.invokeLater(
                () -> {
                    String html = renderer.render(parser.parse(markdown.trim()));

                    String styledHtml =
                            "<html><head><style>"
                                    + "body { font-family: Arial, sans-serif; }"
                                    + "</style></head><body>"
                                    + html
                                    + "</body></html>";

                    this.message.setContentType("text/html");
                    this.message.setText(styledHtml);
                });

        return this;
    }

    private void createUIComponents() {
        message = new JEditorPane();
        message.setContentType("text/html");
        message.setEditable(false);
    }
}
