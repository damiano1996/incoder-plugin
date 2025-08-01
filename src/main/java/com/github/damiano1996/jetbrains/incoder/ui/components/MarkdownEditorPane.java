package com.github.damiano1996.jetbrains.incoder.ui.components;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBHtmlPane;
import com.intellij.ui.components.JBHtmlPaneConfiguration;
import com.intellij.ui.components.JBHtmlPaneStyleConfiguration;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@SuppressWarnings("UnstableApiUsage")
public class MarkdownEditorPane extends JBHtmlPane {

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();
    private String markdown = "";

    public MarkdownEditorPane() {
        super(
                JBHtmlPaneStyleConfiguration.builder()
                        .enableCodeBlocksBackground(true)
                        .enableInlineCodeBackground(true)
                        .build(),
                JBHtmlPaneConfiguration.builder().build());

        setOpaque(false);
        setEditable(false);
    }

    @Override
    public @NotNull String getText() {
        return markdown;
    }

    @Override
    public void setText(String text) {
        this.markdown = text;

        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            String html = renderer.render(parser.parse(markdown));
                            super.setText(html);
                            revalidate();
                            repaint();
                        });
    }
}
