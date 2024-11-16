package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import static com.github.damiano1996.intellijplugin.incoder.InCoderIcons.PLUGIN_ICON;

import com.intellij.ui.JBColor;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;

@Getter
public class AiMessage implements MessageComponent {

    private JPanel mainPanel;
    private JEditorPane message;
    private JLabel aiIconLabel;

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    @Override
    public MessageComponent setMessage(String markdown) {
        this.message.setText(markdown);
        return this;
    }


    private void createUIComponents() {
        message = new MarkdownPanel();
        aiIconLabel = new RoundedLabel(PLUGIN_ICON,60, 60);
        aiIconLabel.setBackground(JBColor.namedColor("Label.background"));
        aiIconLabel.setForeground(JBColor.namedColor("Label.foreground"));
    }
}
