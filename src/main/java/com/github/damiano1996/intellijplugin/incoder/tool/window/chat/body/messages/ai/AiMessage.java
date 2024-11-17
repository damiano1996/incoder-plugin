package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai;

import static com.github.damiano1996.intellijplugin.incoder.InCoderIcons.PLUGIN_ICON;

import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.MarkdownPanel;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.MessageComponent;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.RoundedLabel;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

import javax.swing.*;
import lombok.Getter;

@Getter
public class AiMessage implements MessageComponent {

    private JPanel mainPanel;
    private JEditorPane message;
    private JLabel aiIconLabel;
    private CodeBlock codeBlock;

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    @Override
    public void setProject(Project project) {
        codeBlock.setProject(project);
    }

    @Override
    public MessageComponent setMessage(String message) {
        this.message.setText(message);
        this.codeBlock.setText(message);
        return this;
    }

    private void createUIComponents() {
        message = new MarkdownPanel();
        aiIconLabel = new RoundedLabel(PLUGIN_ICON, 60, 60);
        aiIconLabel.setBackground(JBColor.namedColor("Label.background"));
        aiIconLabel.setForeground(JBColor.namedColor("Label.foreground"));
    }


}
