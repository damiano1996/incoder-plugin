package com.github.damiano1996.jetbrains.incoder.ui.components;

import com.intellij.ui.components.JBTextField;
import java.awt.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PlaceholderTextField extends JBTextField {

    private final String placeholder;
    private final int leftPadding;
    private final int topPadding;

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        if (placeholder == null || placeholder.isEmpty() || !getText().isEmpty()) {
            return;
        }

        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getDisabledTextColor());
        g.drawString(placeholder, leftPadding, pG.getFontMetrics().getMaxAscent() + topPadding);
    }
}
