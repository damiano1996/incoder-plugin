package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.code.CodeMarkdownBlock;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.text.TextMarkdownBlock;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

public class MarkdownPanelTest extends BasePlatformTestCase {

    public void testWrite_StreamACode() {

        MarkdownPanel markdownPanel = new MarkdownPanel(null);

        var streamedMarkdown =
                """
                ```java
                // a java code
                ```
                """;

        simulateStream(streamedMarkdown, markdownPanel);

        var markdownBlocks = markdownPanel.getMarkdownBlocks();

        assertTrue(markdownBlocks.get(0).getText().isEmpty());
        assertInstanceOf(markdownBlocks.get(0), TextMarkdownBlock.class);

        assertEquals(
                """
                // a java code
                """,
                markdownBlocks.get(1).getText());
        assertInstanceOf(markdownBlocks.get(1), CodeMarkdownBlock.class);
        assertEquals("JAVA", ((CodeMarkdownBlock) markdownBlocks.get(1)).getLanguage().getID());

        assertTrue(markdownBlocks.get(2).getText().isEmpty());
        assertInstanceOf(markdownBlocks.get(2), TextMarkdownBlock.class);

        assertEquals(3, markdownBlocks.size());

        dispose(markdownPanel);
    }

    public void testWrite_StreamACodeWithCommentAfter() {

        MarkdownPanel markdownPanel = new MarkdownPanel(null);

        var streamedMarkdown =
                """
                ```java
                // a java code
                ```

                A comment
                """;

        simulateStream(streamedMarkdown, markdownPanel);

        var markdownBlocks = markdownPanel.getMarkdownBlocks();

        assertTrue(markdownBlocks.get(0).getText().isEmpty());
        assertInstanceOf(markdownBlocks.get(0), TextMarkdownBlock.class);

        assertEquals(
                """
                // a java code
                """,
                markdownBlocks.get(1).getText());
        assertInstanceOf(markdownBlocks.get(1), CodeMarkdownBlock.class);
        assertEquals("JAVA", ((CodeMarkdownBlock) markdownBlocks.get(1)).getLanguage().getID());

        assertEquals(
                """

A comment
""",
                markdownBlocks.get(2).getText());
        assertInstanceOf(markdownBlocks.get(2), TextMarkdownBlock.class);

        assertEquals(3, markdownBlocks.size());

        dispose(markdownPanel);
    }

    public void testWrite_StreamACodeWithCommentBefore() {

        MarkdownPanel markdownPanel = new MarkdownPanel(null);

        var streamedMarkdown =
                """
                A comment

                ```java
                // a java code
                ```
                """;

        simulateStream(streamedMarkdown, markdownPanel);

        var markdownBlocks = markdownPanel.getMarkdownBlocks();

        assertEquals(
                """
                A comment

                """,
                markdownBlocks.get(0).getText());
        assertInstanceOf(markdownBlocks.get(0), TextMarkdownBlock.class);

        assertEquals(
                """
                // a java code
                """,
                markdownBlocks.get(1).getText());
        assertInstanceOf(markdownBlocks.get(1), CodeMarkdownBlock.class);
        assertEquals("JAVA", ((CodeMarkdownBlock) markdownBlocks.get(1)).getLanguage().getID());

        assertTrue(markdownBlocks.get(2).getText().isEmpty());
        assertInstanceOf(markdownBlocks.get(2), TextMarkdownBlock.class);

        assertEquals(3, markdownBlocks.size());

        dispose(markdownPanel);
    }

    public void testWrite_StreamMarkdownWithCodesAndANestedMarkdown() {

        MarkdownPanel markdownPanel = new MarkdownPanel(null);

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

        simulateStream(streamedMarkdown, markdownPanel);

        var markdownBlocks = markdownPanel.getMarkdownBlocks();

        assertEquals(
                """
                A comment

                """,
                markdownBlocks.get(0).getText());
        assertInstanceOf(markdownBlocks.get(0), TextMarkdownBlock.class);

        assertEquals(
                """
                // a java code
                """,
                markdownBlocks.get(1).getText());
        assertInstanceOf(markdownBlocks.get(1), CodeMarkdownBlock.class);
        assertEquals("JAVA", ((CodeMarkdownBlock) markdownBlocks.get(1)).getLanguage().getID());

        assertEquals(
                """

Another comment

""",
                markdownBlocks.get(2).getText());
        assertInstanceOf(markdownBlocks.get(2), TextMarkdownBlock.class);

        assertEquals(
                """
                # Title

                ## Section

                ```python
                # a python code
                ```

                A comment

                """,
                markdownBlocks.get(3).getText());
        assertInstanceOf(markdownBlocks.get(3), CodeMarkdownBlock.class);

        assertEquals(
                """

Another comment
""",
                markdownBlocks.get(4).getText());
        assertInstanceOf(markdownBlocks.get(4), TextMarkdownBlock.class);

        assertEquals(5, markdownBlocks.size());

        dispose(markdownPanel);
    }

    public void testWrite_StreamIncompleteCodeBlock() {
        MarkdownPanel markdownPanel = new MarkdownPanel(null);

        var streamedMarkdown =
                """
                Some text
                ```java
                // incomplete code block - no closing backticks
                """;

        simulateStream(streamedMarkdown, markdownPanel);

        var markdownBlocks = markdownPanel.getMarkdownBlocks();

        assertInstanceOf(markdownBlocks.get(0), TextMarkdownBlock.class);
        assertInstanceOf(markdownBlocks.get(1), CodeMarkdownBlock.class);

        assertEquals(2, markdownBlocks.size());

        dispose(markdownPanel);
    }

    public void testWrite_StreamEmptyCodeBlock() {
        MarkdownPanel markdownPanel = new MarkdownPanel(null);

        var streamedMarkdown =
                """
                ```java
                ```
                """;

        simulateStream(streamedMarkdown, markdownPanel);

        var markdownBlocks = markdownPanel.getMarkdownBlocks();

        assertInstanceOf(markdownBlocks.get(0), TextMarkdownBlock.class);
        assertTrue(markdownBlocks.get(0).getText().isEmpty());

        assertInstanceOf(markdownBlocks.get(1), CodeMarkdownBlock.class);
        assertTrue(markdownBlocks.get(1).getText().isEmpty());

        assertInstanceOf(markdownBlocks.get(2), TextMarkdownBlock.class);
        assertTrue(markdownBlocks.get(2).getText().isEmpty());

        assertEquals(3, markdownBlocks.size());

        dispose(markdownPanel);
    }

    public void testWrite_StreamWhitespaceOnlyCodeBlock() {
        MarkdownPanel markdownPanel = new MarkdownPanel(null);

        var streamedMarkdown =
                """
                ```java


                ```
                """;

        simulateStream(streamedMarkdown, markdownPanel);

        var markdownBlocks = markdownPanel.getMarkdownBlocks();

        assertInstanceOf(markdownBlocks.get(0), TextMarkdownBlock.class);
        assertTrue(markdownBlocks.get(0).getText().isEmpty());

        assertInstanceOf(markdownBlocks.get(1), CodeMarkdownBlock.class);
        assertFalse(markdownBlocks.get(1).getText().isEmpty());

        assertInstanceOf(markdownBlocks.get(2), TextMarkdownBlock.class);
        assertTrue(markdownBlocks.get(2).getText().isEmpty());

        assertEquals(3, markdownBlocks.size());

        dispose(markdownPanel);
    }

    public void testWrite_StreamInvalidLanguage() {
        MarkdownPanel markdownPanel = new MarkdownPanel(null);

        var streamedMarkdown =
                """
                ```nonexistentlanguage
                some code
                ```
                """;

        simulateStream(streamedMarkdown, markdownPanel);

        var markdownBlocks = markdownPanel.getMarkdownBlocks();

        assertInstanceOf(markdownBlocks.get(0), TextMarkdownBlock.class);
        assertTrue(markdownBlocks.get(0).getText().isEmpty());

        assertInstanceOf(markdownBlocks.get(1), CodeMarkdownBlock.class);
        assertFalse(markdownBlocks.get(1).getText().isEmpty());

        assertInstanceOf(markdownBlocks.get(2), TextMarkdownBlock.class);
        assertTrue(markdownBlocks.get(2).getText().isEmpty());

        assertEquals(3, markdownBlocks.size());

        dispose(markdownPanel);
    }

    public void testWrite_StreamConsecutiveCodeBlocks() {
        MarkdownPanel markdownPanel = new MarkdownPanel(null);

        var streamedMarkdown =
                """
                ```java
                // first block
                ```
                ```python
                # second block immediately after
                ```
                """;

        simulateStream(streamedMarkdown, markdownPanel);

        var markdownBlocks = markdownPanel.getMarkdownBlocks();

        assertInstanceOf(markdownBlocks.get(0), TextMarkdownBlock.class);
        assertTrue(markdownBlocks.get(0).getText().isEmpty());

        assertInstanceOf(markdownBlocks.get(1), CodeMarkdownBlock.class);
        assertFalse(markdownBlocks.get(1).getText().isEmpty());
        assertEquals(
                """
                // first block
                """,
                markdownBlocks.get(1).getText());

        assertInstanceOf(markdownBlocks.get(2), TextMarkdownBlock.class);
        assertTrue(markdownBlocks.get(2).getText().isEmpty());

        assertInstanceOf(markdownBlocks.get(3), CodeMarkdownBlock.class);
        assertFalse(markdownBlocks.get(3).getText().isEmpty());
        assertEquals(
                """
                # second block immediately after
                """,
                markdownBlocks.get(3).getText());

        assertInstanceOf(markdownBlocks.get(4), TextMarkdownBlock.class);
        assertTrue(markdownBlocks.get(4).getText().isEmpty());

        assertEquals(5, markdownBlocks.size());

        dispose(markdownPanel);
    }

    private static void dispose(@NotNull MarkdownPanel markdownPanel) {
        markdownPanel.getMarkdownBlocks().stream()
                .filter(markdownBlock -> markdownBlock instanceof CodeMarkdownBlock)
                .forEach(markdownBlock -> ((CodeMarkdownBlock) markdownBlock).dispose());
    }

    private static void simulateStream(
            @NotNull String streamedMarkdown, MarkdownPanel markdownPanel) {
        String[] tokens = streamedMarkdown.split("(?<=\\n|\\s)");
        for (String token : tokens) {
            markdownPanel.write(token);
        }

        markdownPanel.streamClosed();
    }
}
