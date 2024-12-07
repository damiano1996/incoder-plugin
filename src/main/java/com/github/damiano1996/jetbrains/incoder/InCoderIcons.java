package com.github.damiano1996.jetbrains.incoder;

import com.intellij.openapi.util.IconLoader;
import javax.swing.*;

/** This class provides access to icons used in the InCoder plugin. */
public class InCoderIcons {
    /**
     * The icon representing the InCoder plugin. It is loaded from the resource file located at
     * /META-INF/pluginIcon.svg.
     */
    public static Icon PLUGIN_ICON =
            IconLoader.getIcon("/META-INF/pluginIcon.svg", InCoderIcons.class);
}
