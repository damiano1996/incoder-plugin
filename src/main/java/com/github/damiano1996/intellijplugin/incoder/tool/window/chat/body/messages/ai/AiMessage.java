package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai;

import static com.github.damiano1996.intellijplugin.incoder.InCoderIcons.PLUGIN_ICON;

import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.MarkdownPanel;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.MessageComponent;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.RoundedLabel;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import javax.swing.*;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public class AiMessage implements MessageComponent {

    @Nullable private Project project;

    private JPanel mainPanel;
    private MarkdownPanel markdownPanel;
    private JLabel aiIconLabel;
    private JScrollPane scrollPane;

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public void setProject(@Nullable Project project) {
        this.project = project;
        this.markdownPanel.setProject(project);
    }

    @Override
    public void write(String token) {
        this.markdownPanel.write(token);
    }

    @Override
    public String getFullText() {
        return "";
    }

    private void createUIComponents() {
        scrollPane = new JBScrollPane();

        markdownPanel = new MarkdownPanel();
        aiIconLabel = new RoundedLabel(PLUGIN_ICON, 60, 60);
        aiIconLabel.setBackground(JBColor.namedColor("Label.background"));
        aiIconLabel.setForeground(JBColor.namedColor("Label.foreground"));
    }
}
