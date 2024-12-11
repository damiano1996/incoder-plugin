package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.MarkdownPanel;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class CodeMarkdownBlockTest extends BasePlatformTestCase {

    public void testWriteMethod() {
        MarkdownPanel mockMarkdownPanel = new MarkdownPanel(myFixture.getProject());

        CodeMarkdownBlock codeBlock = new CodeMarkdownBlock(mockMarkdownPanel, "java", "");

        var code =
                """
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }
                """;

        String[] tokens = code.split("(?<=\\n|\\s)");
        for (String token : tokens) {
            codeBlock.write(token);
        }

        assertEquals(code, codeBlock.getEditor().getDocument().getText());
    }
}
