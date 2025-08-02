package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.blocks.code;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.ChatBody;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.MarkdownChatMessage;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.markdown.blocks.MarkdownBlock;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public class CodeMarkdownBlockTest extends BasePlatformTestCase {

    private static void streamAndAssertResult(
            String streamedCode, @NotNull CodeMarkdownBlock codeBlock, String expectedCode) {
        simulateStreamOfTokens(streamedCode, codeBlock);
        assertExpectedCode(expectedCode, codeBlock);
    }

    private static void simulateStreamOfTokens(
            @NotNull String streamedCode, @NotNull CodeMarkdownBlock codeMarkdownBlock) {
        String[] tokens = streamedCode.split("(?<=\\n|\\s)");

        boolean streamCompleted = false;

        try {
            for (String token : tokens) {
                codeMarkdownBlock.write(token);
            }
            streamCompleted = true;
        } catch (StopStreamException ignored) {

        }

        if (streamCompleted) {
            try {
                codeMarkdownBlock.closeStream();
            } catch (StopStreamException ignored) {
            }
        }
    }

    private static void assertExpectedCode(
            String expectedCode, @NotNull CodeMarkdownBlock codeBlock) {
        try {
            waitForAppLeakingThreads(100, TimeUnit.MILLISECONDS);
        } catch (StopStreamException ignored) {
        }

        assertEquals(expectedCode, codeBlock.getText());

        codeBlock.getEditorPanel().dispose();
    }

    public void testWrite_StreamJavaCode() {
        CodeMarkdownBlock codeBlock = getCodeMarkdownBlock("java");

        var streamedCode =
                """
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                """;

        streamAndAssertResult(streamedCode, codeBlock, streamedCode);
    }

    private @NotNull CodeMarkdownBlock getCodeMarkdownBlock(String language) {
        MarkdownChatMessage mockMarkdownChatMessage =
                new MarkdownChatMessage(new ChatBody(e -> {})) {

                    private boolean initialized = false;

                    @Override
                    public void next(MarkdownBlock markdownBlock) {
                        if (!initialized) {
                            super.next(markdownBlock);
                            initialized = !initialized;
                        } else {
                            throw new StopStreamException();
                        }
                    }
                };

        var codeBlock = new CodeMarkdownBlock(mockMarkdownChatMessage, language, "");
        mockMarkdownChatMessage.next(codeBlock);

        return codeBlock;
    }

    public void testWrite_StreamJavaCodeWithMarkdownDelimiterAndAdditionalComments() {
        CodeMarkdownBlock codeBlock = getCodeMarkdownBlock("java");

        var streamedCode =
                """
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                ```

                Another comment
                """;

        var expectedCode =
                """
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                """;

        streamAndAssertResult(streamedCode, codeBlock, expectedCode);
    }

    public void testWrite_StreamTwoCodes() {
        CodeMarkdownBlock codeBlock = getCodeMarkdownBlock("java");

        var streamedCode =
                """
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                ```

                Another comment

                ```java
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                ```

                Another comment
                """;

        var expectedCode =
                """
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                """;

        streamAndAssertResult(streamedCode, codeBlock, expectedCode);
    }

    public void testWrite_StreamJavaCodeWithMarkdownDelimiter() {
        CodeMarkdownBlock codeBlock = getCodeMarkdownBlock("java");

        var streamedCode =
                """
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                ```

                """;

        var expectedCode =
                """
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                """;

        streamAndAssertResult(streamedCode, codeBlock, expectedCode);
    }

    public void testWrite_StreamMarkdown() {
        CodeMarkdownBlock codeBlock = getCodeMarkdownBlock("Markdown");

        var streamedCode =
                """
                # Title

                ## Section

                ```java
                // sample code
                ```

                ```
                """;

        var expectedCode =
                """
                # Title

                ## Section

                ```java
                // sample code
                ```

                """;

        streamAndAssertResult(streamedCode, codeBlock, expectedCode);
    }

    public void testWrite_StreamMarkdownWithMarkdownDelimiter() {
        CodeMarkdownBlock codeBlock = getCodeMarkdownBlock("Markdown");

        var streamedCode =
                """
                # Title

                ## Section 1

                ```java
                // sample code
                ```

                ## Section 2

                ```

                Another comment
                """;

        var expectedCode =
                """
                # Title

                ## Section 1

                ```java
                // sample code
                ```

                ## Section 2

                """;

        streamAndAssertResult(streamedCode, codeBlock, expectedCode);
    }

    public void testWrite_StreamTwoMarkdown() {
        CodeMarkdownBlock codeBlock = getCodeMarkdownBlock("Markdown");

        var streamedCode =
                """
                # Title

                ## Section 1

                ```java
                // sample code
                ```

                ## Section 2

                ```python
                # sample code
                ```

                ```

                Another comment

                ```Markdown
                # Title

                ## Section 1

                ```java
                // sample code
                ```

                ## Section 2

                ```
                """;

        var expectedCode =
                """
                # Title

                ## Section 1

                ```java
                // sample code
                ```

                ## Section 2

                ```python
                # sample code
                ```

                """;

        streamAndAssertResult(streamedCode, codeBlock, expectedCode);
    }

    private static class StopStreamException extends RuntimeException {}
}
