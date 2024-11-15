package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import com.intellij.util.ui.HtmlPanel;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import lombok.Getter;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

@Getter
public class AiMessage implements MessageComponent {

    private JPanel mainPanel;
    private JEditorPane message;

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    @Override
    public MessageComponent setMessage(String markdown) {
            SwingUtilities.invokeLater(() -> this.message.setText(markdown));


        return this;
    }

    private void createUIComponents() {
        message = new MarkdownPanel();
    }
}
