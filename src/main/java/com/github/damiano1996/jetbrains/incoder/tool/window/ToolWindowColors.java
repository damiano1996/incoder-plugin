package com.github.damiano1996.jetbrains.incoder.tool.window;

import com.intellij.ui.JBColor;
import java.awt.*;

public class ToolWindowColors {

    public static final JBColor TRANSPARENT =
            new JBColor(new Color(0, 0, 0, 0), new Color(0, 0, 0, 0));

    public static JBColor BADGE_BACKGROUND =
            new JBColor(new Color(255, 255, 255, 255), new Color(70, 70, 70, 255));
    public static JBColor BADGE_FOREGROUND =
            new JBColor(new Color(40, 40, 40, 255), new Color(190, 190, 190, 255));

    public static JBColor USER_MESSAGE_BACKGROUND =
            new JBColor(new Color(40, 40, 40, 255), new Color(190, 190, 190, 255));
    public static JBColor USER_MESSAGE_FOREGROUND =
            new JBColor(new Color(250, 250, 250, 255), new Color(40, 40, 40, 255));

    public static JBColor AI_MESSAGE_BACKGROUND = TRANSPARENT;
    public static JBColor AI_MESSAGE_FOREGROUND =
            new JBColor(new Color(40, 40, 40, 255), new Color(210, 210, 210, 255));

    public static JBColor INTELLIGENT_ACTION_BACKGROUND = BADGE_FOREGROUND;
    public static JBColor INTELLIGENT_ACTION_FOREGROUND = BADGE_BACKGROUND;
}
