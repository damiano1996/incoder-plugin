package com.github.damiano1996.intellijplugin.incoder.completion.states.preview.renderer;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import java.awt.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class PreviewInlayRenderer implements EditorCustomElementRenderer {

    private final String preview;

    public PreviewInlayRenderer(String preview) {
        log.debug("Preview of: '{}'", preview);
        this.preview = preview;
    }

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        return inlay.getEditor()
                .getContentComponent()
                .getFontMetrics(inlay.getEditor().getColorsScheme().getFont(EditorFontType.PLAIN))
                .stringWidth(preview);
    }

    @Override
    public void paint(
            @NotNull Inlay inlay,
            @NotNull Graphics g,
            @NotNull Rectangle targetRegion,
            @NotNull TextAttributes textAttributes) {
        Editor editor = inlay.getEditor();
        @SuppressWarnings("UseJBColor")
        Color color =
                new Color(
                        editor.getColorsScheme().getDefaultForeground().getRed(),
                        editor.getColorsScheme().getDefaultForeground().getGreen(),
                        editor.getColorsScheme().getDefaultForeground().getBlue(),
                        Math.min(
                                editor.getColorsScheme().getDefaultForeground().getAlpha() - 100,
                                100));
        JBColor jbColor = new JBColor(color, color);

        g.setColor(jbColor);

        g.setFont(editor.getColorsScheme().getFont(EditorFontType.PLAIN));
        FontMetrics fontMetrics = g.getFontMetrics();
        int baseline = targetRegion.y + fontMetrics.getAscent() + 2;

        g.drawString(preview, targetRegion.x, baseline);
    }
}
