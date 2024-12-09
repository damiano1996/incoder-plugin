package com.github.damiano1996.jetbrains.incoder.ui.components;

import com.github.damiano1996.jetbrains.incoder.tool.window.ToolWindowColors;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

public class DescriptionLabel extends JBTextArea {

    public DescriptionLabel(@NotNull @NlsContexts.Label String text) {
        super(text);
        setForeground(JBColor.namedColor("Label.infoForeground"));
        setBackground(ToolWindowColors.TRANSPARENT);
        setWrapStyleWord(true);
        setLineWrap(true);
        setEditable(false);
        setFocusable(false);
        setFont(UIUtil.getLabelFont());
    }

    @Override
    public void setCaretPosition(int position) {}

    @Override
    public boolean isFocusable() {
        return false;
    }
}
