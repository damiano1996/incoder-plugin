package com.github.damiano1996.jetbrains.incoder.ui.components;

import com.intellij.ui.components.JBHtmlPane;
import com.intellij.ui.components.JBHtmlPaneConfiguration;
import com.intellij.ui.components.JBHtmlPaneStyleConfiguration;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.data.DataHolder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@SuppressWarnings("UnstableApiUsage")
public class MarkdownEditorPane extends JBHtmlPane {

    private static final DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(Extensions.ALL);
    private static final Parser PARSER = Parser.builder(OPTIONS).build();
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();

    private String markdown = "";

    public MarkdownEditorPane() {
        super(getStyleConfiguration(), getPaneConfiguration());

        setOpaque(false);
        setEditable(false);
    }

    private static @NotNull JBHtmlPaneStyleConfiguration getStyleConfiguration() {
        return JBHtmlPaneStyleConfiguration.builder()
                .enableCodeBlocksBackground(true)
                .enableInlineCodeBackground(true)
                .build();
    }

    private static @NotNull JBHtmlPaneConfiguration getPaneConfiguration() {
        return JBHtmlPaneConfiguration.builder().build();
    }

    @Override
    public @NotNull String getText() {
        return markdown;
    }

    @Override
    public void setText(String text) {
        this.markdown = text;
        String html = RENDERER.render(PARSER.parse(markdown));
        super.setText(html);
    }
}
