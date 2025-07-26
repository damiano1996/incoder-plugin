package com.github.damiano1996.jetbrains.incoder.tool.window.chat;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ChatConstants {

    // UI Text Constants
    public static final String PROMPT_PLACEHOLDER = "Enter a prompt...";
    public static final String SEND_MESSAGE_TOOLTIP = "Send message";
    public static final String STOP_TOOLTIP = "Stop feature coming soon";

    // Error Messages
    public static final String LANGUAGE_MODEL_NOT_READY =
            "The Language Model Service is not ready. "
                    + "Please, configure the Chat and the Server from Settings.";

    public static final String JSON_EOF_ERROR_TEMPLATE =
            """
            <html>Response parsing failed. This could be due to:<br>
            &nbsp;&nbsp;- Insufficient max tokens<br>
            &nbsp;&nbsp;- Incomplete model response<br>
            <br>
            Additional details:<br>
            %s
            </html>
            """;

    public static final String TOOL_INVOCATION_ERROR_TEMPLATE =
            """
            <html>Tool invocation error occurred.<br>
            &nbsp;&nbsp;- Model failed to call tool correctly<br>
            &nbsp;&nbsp;- Incorrect tool function or parameter format<br>
            <br>
            Additional details:<br>
            %s
            </html>
            """;

    public static final String UNEXPECTED_ERROR_TEMPLATE =
            """
            <html>An unexpected error occurred during response generation.<br>
            <br>
            Additional details:<br>
            %s
            </html>
            """;
}
