package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages;

/** A stream writer interface used to manage text input in a chat-like environment. */
public interface StreamWriter {

    /**
     * Writes a single token (string) to the internal buffer.
     *
     * @param token The string token to be written.
     */
    void write(String token);

    /**
     * Retrieves the full text that has been written so far.
     *
     * @return A concatenated string of all tokens written to the buffer.
     */
    String getFullText();
}
