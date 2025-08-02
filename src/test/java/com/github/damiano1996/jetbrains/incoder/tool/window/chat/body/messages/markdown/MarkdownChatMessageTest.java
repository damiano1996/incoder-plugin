package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.ChatBody;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.blocks.code.CodeMarkdownBlock;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.blocks.text.TextMarkdownBlock;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

public class MarkdownChatMessageTest extends BasePlatformTestCase {

    private ChatBody chatBody;
    private MarkdownChatMessage markdownChatMessage;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        chatBody = new ChatBody(e -> {});

        markdownChatMessage = new MarkdownChatMessage(chatBody);
    }

    @Override
    public void tearDown() throws Exception {
        chatBody.getMessages().stream()
                .filter(markdownBlock -> markdownBlock instanceof CodeMarkdownBlock)
                .forEach(markdownBlock -> ((CodeMarkdownBlock) markdownBlock).dispose());

        super.tearDown();
    }

    public void testWrite_StreamACode() {
        var streamedMarkdown =
                """
                ```java
                // a java code
                ```
                """;

        simulateStream(streamedMarkdown, markdownChatMessage);

        assertTrue(chatBody.getMessages().get(0).getText().isEmpty());
        assertInstanceOf(chatBody.getMessages().get(0), TextMarkdownBlock.class);

        assertEquals(
                """
                // a java code
                """,
                chatBody.getMessages().get(1).getText());
        assertInstanceOf(chatBody.getMessages().get(1), CodeMarkdownBlock.class);
        assertEquals(
                "JAVA", ((CodeMarkdownBlock) chatBody.getMessages().get(1)).getLanguage().getID());

        assertTrue(chatBody.getMessages().get(2).getText().isEmpty());
        assertInstanceOf(chatBody.getMessages().get(2), TextMarkdownBlock.class);

        assertEquals(3, chatBody.getMessages().size());
    }

    public void testWrite_StreamACodeWithCommentAfter() {
        var streamedMarkdown =
                """
                ```java
                // a java code
                ```

                A comment
                """;

        simulateStream(streamedMarkdown, markdownChatMessage);

        assertTrue(chatBody.getMessages().get(0).getText().isEmpty());
        assertInstanceOf(chatBody.getMessages().get(0), TextMarkdownBlock.class);

        assertEquals(
                """
                // a java code
                """,
                chatBody.getMessages().get(1).getText());
        assertInstanceOf(chatBody.getMessages().get(1), CodeMarkdownBlock.class);
        assertEquals(
                "JAVA", ((CodeMarkdownBlock) chatBody.getMessages().get(1)).getLanguage().getID());

        assertEquals(
                """

A comment
""",
                chatBody.getMessages().get(2).getText());
        assertInstanceOf(chatBody.getMessages().get(2), TextMarkdownBlock.class);

        assertEquals(3, chatBody.getMessages().size());
    }

    public void testWrite_StreamACodeWithCommentBefore() {
        var streamedMarkdown =
                """
                A comment

                ```java
                // a java code
                ```
                """;

        simulateStream(streamedMarkdown, markdownChatMessage);

        assertEquals(
                """
                A comment

                """,
                chatBody.getMessages().get(0).getText());
        assertInstanceOf(chatBody.getMessages().get(0), TextMarkdownBlock.class);

        assertEquals(
                """
                // a java code
                """,
                chatBody.getMessages().get(1).getText());
        assertInstanceOf(chatBody.getMessages().get(1), CodeMarkdownBlock.class);
        assertEquals(
                "JAVA", ((CodeMarkdownBlock) chatBody.getMessages().get(1)).getLanguage().getID());

        assertTrue(chatBody.getMessages().get(2).getText().isEmpty());
        assertInstanceOf(chatBody.getMessages().get(2), TextMarkdownBlock.class);

        assertEquals(3, chatBody.getMessages().size());
    }

    public void testWrite_StreamMarkdownWithCodesAndANestedMarkdown() {
        var streamedMarkdown =
                """
                A comment

                ```java
                // a java code
                ```

                Another comment

                ```Markdown
                # Title

                ## Section

                ```python
                # a python code
                ```

                A comment

                ```

                Another comment
                """;

        simulateStream(streamedMarkdown, markdownChatMessage);

        assertEquals(
                """
                A comment

                """,
                chatBody.getMessages().get(0).getText());
        assertInstanceOf(chatBody.getMessages().get(0), TextMarkdownBlock.class);

        assertEquals(
                """
                // a java code
                """,
                chatBody.getMessages().get(1).getText());
        assertInstanceOf(chatBody.getMessages().get(1), CodeMarkdownBlock.class);
        assertEquals(
                "JAVA", ((CodeMarkdownBlock) chatBody.getMessages().get(1)).getLanguage().getID());

        assertEquals(
                """

Another comment

""",
                chatBody.getMessages().get(2).getText());
        assertInstanceOf(chatBody.getMessages().get(2), TextMarkdownBlock.class);

        assertEquals(
                """
                # Title

                ## Section

                ```python
                # a python code
                ```

                A comment

                """,
                chatBody.getMessages().get(3).getText());
        assertInstanceOf(chatBody.getMessages().get(3), CodeMarkdownBlock.class);

        assertEquals(
                """

Another comment
""",
                chatBody.getMessages().get(4).getText());
        assertInstanceOf(chatBody.getMessages().get(4), TextMarkdownBlock.class);

        assertEquals(5, chatBody.getMessages().size());
    }

    public void testWrite_StreamIncompleteCodeBlock() {
        var streamedMarkdown =
                """
                Some text
                ```java
                // incomplete code block - no closing backticks
                """;

        simulateStream(streamedMarkdown, markdownChatMessage);

        assertInstanceOf(chatBody.getMessages().get(0), TextMarkdownBlock.class);
        assertInstanceOf(chatBody.getMessages().get(1), CodeMarkdownBlock.class);

        assertEquals(2, chatBody.getMessages().size());
    }

    public void testWrite_StreamEmptyCodeBlock() {
        var streamedMarkdown =
                """
                ```java
                ```
                """;

        simulateStream(streamedMarkdown, markdownChatMessage);

        assertInstanceOf(chatBody.getMessages().get(0), TextMarkdownBlock.class);
        assertTrue(chatBody.getMessages().get(0).getText().isEmpty());

        assertInstanceOf(chatBody.getMessages().get(1), CodeMarkdownBlock.class);
        assertTrue(chatBody.getMessages().get(1).getText().isEmpty());

        assertInstanceOf(chatBody.getMessages().get(2), TextMarkdownBlock.class);
        assertTrue(chatBody.getMessages().get(2).getText().isEmpty());

        assertEquals(3, chatBody.getMessages().size());
    }

    public void testWrite_StreamWhitespaceOnlyCodeBlock() {
        var streamedMarkdown =
                """
                ```java


                ```
                """;

        simulateStream(streamedMarkdown, markdownChatMessage);

        assertInstanceOf(chatBody.getMessages().get(0), TextMarkdownBlock.class);
        assertTrue(chatBody.getMessages().get(0).getText().isEmpty());

        assertInstanceOf(chatBody.getMessages().get(1), CodeMarkdownBlock.class);
        assertFalse(chatBody.getMessages().get(1).getText().isEmpty());

        assertInstanceOf(chatBody.getMessages().get(2), TextMarkdownBlock.class);
        assertTrue(chatBody.getMessages().get(2).getText().isEmpty());

        assertEquals(3, chatBody.getMessages().size());
    }

    public void testWrite_StreamInvalidLanguage() {
        var streamedMarkdown =
                """
                ```nonexistentlanguage
                some code
                ```
                """;

        simulateStream(streamedMarkdown, markdownChatMessage);

        assertInstanceOf(chatBody.getMessages().get(0), TextMarkdownBlock.class);
        assertTrue(chatBody.getMessages().get(0).getText().isEmpty());

        assertInstanceOf(chatBody.getMessages().get(1), CodeMarkdownBlock.class);
        assertFalse(chatBody.getMessages().get(1).getText().isEmpty());

        assertInstanceOf(chatBody.getMessages().get(2), TextMarkdownBlock.class);
        assertTrue(chatBody.getMessages().get(2).getText().isEmpty());

        assertEquals(3, chatBody.getMessages().size());
    }

    public void testWrite_StreamConsecutiveCodeBlocks() {
        var streamedMarkdown =
                """
                ```java
                // first block
                ```
                ```python
                # second block immediately after
                ```
                """;

        simulateStream(streamedMarkdown, markdownChatMessage);

        assertInstanceOf(chatBody.getMessages().get(0), TextMarkdownBlock.class);
        assertTrue(chatBody.getMessages().get(0).getText().isEmpty());

        assertInstanceOf(chatBody.getMessages().get(1), CodeMarkdownBlock.class);
        assertFalse(chatBody.getMessages().get(1).getText().isEmpty());
        assertEquals(
                """
                // first block
                """,
                chatBody.getMessages().get(1).getText());

        assertInstanceOf(chatBody.getMessages().get(2), TextMarkdownBlock.class);
        assertTrue(chatBody.getMessages().get(2).getText().isEmpty());

        assertInstanceOf(chatBody.getMessages().get(3), CodeMarkdownBlock.class);
        assertFalse(chatBody.getMessages().get(3).getText().isEmpty());
        assertEquals(
                """
                # second block immediately after
                """,
                chatBody.getMessages().get(3).getText());

        assertInstanceOf(chatBody.getMessages().get(4), TextMarkdownBlock.class);
        assertTrue(chatBody.getMessages().get(4).getText().isEmpty());

        assertEquals(5, chatBody.getMessages().size());
    }

    private static void simulateStream(
            @NotNull String streamedMarkdown, MarkdownChatMessage markdownChatMessage) {
        String[] tokens = streamedMarkdown.split("(?<=\\n|\\s)");
        for (String token : tokens) {
            markdownChatMessage.write(token);
        }

        markdownChatMessage.closeStream();
    }
}
