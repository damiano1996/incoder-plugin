package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

public interface StreamWriter {

    void write(String token);

    String getFullText();
}
