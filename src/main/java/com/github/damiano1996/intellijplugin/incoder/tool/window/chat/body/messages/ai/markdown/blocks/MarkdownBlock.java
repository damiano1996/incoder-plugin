package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks;

import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.StreamWriter;

import javax.swing.*;

public interface MarkdownBlock extends StreamWriter {

    JComponent getComponent();
}
