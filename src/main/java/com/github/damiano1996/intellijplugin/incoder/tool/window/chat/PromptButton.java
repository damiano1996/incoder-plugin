package com.github.damiano1996.intellijplugin.incoder.tool.window.chat;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.JBColor;
import com.intellij.ui.RoundedLineBorder;
import com.intellij.util.ui.JButtonAction;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.intellij.ide.IdeTooltipManager.setBorder;

public class PromptButton extends JButton {

    @Getter
    private final String prompt;

    public PromptButton(String text, String prompt) {
        this.prompt = prompt;

        setBorder(new RoundedLineBorder(JBColor.BLUE, 10, 3));

//        setContentAreaFilled(false);
//        setFocusPainted(false);
//        setBorderPainted(false);
//        setForeground(Color.WHITE);
//        setBackground(new Color(0, 123, 255));
    }
}
