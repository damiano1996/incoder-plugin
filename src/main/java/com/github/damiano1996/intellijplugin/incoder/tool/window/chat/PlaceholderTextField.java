package com.github.damiano1996.intellijplugin.incoder.tool.window.chat;

import com.intellij.ui.components.JBTextField;
import java.awt.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PlaceholderTextField extends JBTextField {

    private String placeholder;

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        if (placeholder == null || placeholder.isEmpty() || !getText().isEmpty()) {
            return;
        }

        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getDisabledTextColor());
        g.drawString(
                placeholder,
                getInsets().left + 10,
                pG.getFontMetrics().getMaxAscent() + getInsets().top + 5);
    }
}
