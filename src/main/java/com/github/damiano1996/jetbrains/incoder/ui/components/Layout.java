package com.github.damiano1996.jetbrains.incoder.ui.components;

import java.awt.*;
import javax.swing.*;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class Layout {

    public static @NotNull JPanel componentToRight(JComponent chatToolBar) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(chatToolBar, BorderLayout.EAST);
        return wrapper;
    }
}
