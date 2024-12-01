package com.github.damiano1996.intellijplugin.incoder.ui.components;

import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;

public class DescriptionLabel extends JBLabel {

    public DescriptionLabel(@NotNull @NlsContexts.Label String text) {
        super(text);
        setForeground(JBColor.namedColor("Label.infoForeground"));
    }
}
