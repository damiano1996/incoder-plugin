package com.github.damiano1996.jetbrains.incoder.language.model.client.tools;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class EditorToolTest extends BasePlatformTestCase {

    private EditorTool editorTool;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        editorTool = new EditorTool();
    }

    public void testApplyPatches_SingleLineReplacement() {
        String originalContent = "line1\nline2\nline3";
        List<EditorTool.PatchHunk> patchHunks =
                List.of(new EditorTool.PatchHunk(2, 2, "line2", "modified line2"));

        String result = getApplyPatches(originalContent, patchHunks);

        assertEquals("line1\nmodified line2\nline3", result);
    }

    public void testApplyPatches_MultipleLineReplacement() {
        String originalContent = "line1\nline2\nline3\nline4";
        List<EditorTool.PatchHunk> patchHunks =
                List.of(new EditorTool.PatchHunk(2, 3, "line2\nline3", "new line2\nnew line3"));

        String result = getApplyPatches(originalContent, patchHunks);

        assertEquals("line1\nnew line2\nnew line3\nline4", result);
    }

    public void testApplyPatches_MultiplePatchHunks() {
        String originalContent = "line1\nline2\nline3\nline4\nline5";
        List<EditorTool.PatchHunk> patchHunks =
                Arrays.asList(
                        new EditorTool.PatchHunk(1, 1, "line1", "modified line1"),
                        new EditorTool.PatchHunk(4, 4, "line4", "modified line4"));

        String result = getApplyPatches(originalContent, patchHunks);

        assertEquals("modified line1\nline2\nline3\nmodified line4\nline5", result);
    }

    public void testApplyPatches_ReverseOrderApplication() {
        // Test that patches are applied in reverse order (higher line numbers first)
        String originalContent = "line1\nline2\nline3\nline4";
        List<EditorTool.PatchHunk> patchHunks =
                Arrays.asList(
                        new EditorTool.PatchHunk(1, 1, "line1", "A"),
                        new EditorTool.PatchHunk(3, 3, "line3", "C"));

        String result = getApplyPatches(originalContent, patchHunks);

        assertEquals("A\nline2\nC\nline4", result);
    }

    public void testApplyPatches_InsertNewLines() {
        String originalContent = "line1\nline3";
        List<EditorTool.PatchHunk> patchHunks =
                List.of(new EditorTool.PatchHunk(1, 1, "line2", "line1\nline2"));

        String result = getApplyPatches(originalContent, patchHunks);

        assertEquals("line1\nline2\nline3", result);
    }

    public void testApplyPatches_DeleteLines() {
        String originalContent = "line1\nline2\nline3\nline4";
        List<EditorTool.PatchHunk> patchHunks =
                List.of(new EditorTool.PatchHunk(2, 4, "line2\nline3", "line4"));

        String result = getApplyPatches(originalContent, patchHunks);

        assertEquals("line1\nline4", result);
    }

    public void testApplyPatches_EmptyContent() {
        String originalContent = "";
        List<EditorTool.PatchHunk> patchHunks =
                List.of(new EditorTool.PatchHunk(1, 1, "", "new content"));

        String result = getApplyPatches(originalContent, patchHunks);

        assertEquals("new content", result);
    }

    public void testApplyPatches_SingleLineContent() {
        String originalContent = "single line";
        List<EditorTool.PatchHunk> patchHunks =
                List.of(new EditorTool.PatchHunk(1, 1, "single line", "modified single line"));

        String result = getApplyPatches(originalContent, patchHunks);

        assertEquals("modified single line", result);
    }

    public void testApplyPatches_OutOfBoundsStartLine() {
        String originalContent = "line1\nline2";
        List<EditorTool.PatchHunk> patchHunks =
                List.of(new EditorTool.PatchHunk(0, 1, "line1", "modified"));

        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    getApplyPatches(originalContent, patchHunks);
                });
    }

    public void testApplyPatches_OutOfBoundsEndLine() {
        String originalContent = "line1\nline2";
        List<EditorTool.PatchHunk> patchHunks =
                List.of(new EditorTool.PatchHunk(1, 5, "line1", "modified"));

        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    getApplyPatches(originalContent, patchHunks);
                });
    }

    public void testApplyPatches_InvalidLineRange() {
        String originalContent = "line1\nline2\nline3";
        List<EditorTool.PatchHunk> patchHunks =
                List.of(new EditorTool.PatchHunk(3, 1, "invalid", "range"));

        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    getApplyPatches(originalContent, patchHunks);
                });
    }

    public void testApplyPatches_MultilineNewContent() {
        String originalContent = "line1\nline2\nline3";
        List<EditorTool.PatchHunk> patchHunks =
                List.of(
                        new EditorTool.PatchHunk(
                                2, 2, "line2", "new line2\nextra line\nanother line"));

        String result = getApplyPatches(originalContent, patchHunks);

        assertEquals("line1\nnew line2\nextra line\nanother line\nline3", result);
    }

    public void testApplyPatches_PreservesEmptyLines() {
        String originalContent = "line1\n\nline3";
        List<EditorTool.PatchHunk> patchHunks =
                List.of(new EditorTool.PatchHunk(2, 2, "", "filled line"));

        String result = getApplyPatches(originalContent, patchHunks);

        assertEquals("line1\nfilled line\nline3", result);
    }

    public void testApplyPatches_WindowsLineEndings() {
        String originalContent = "line1\r\nline2\r\nline3";
        List<EditorTool.PatchHunk> patchHunks =
                List.of(new EditorTool.PatchHunk(2, 2, "line2", "modified line2"));

        String result = getApplyPatches(originalContent, patchHunks);

        // The method should handle different line endings and normalize to \n
        assertEquals("line1\nmodified line2\nline3", result);
    }

    public void testApplyPatches_MixedLineEndings() {
        String originalContent = "line1\nline2\r\nline3\rline4";
        List<EditorTool.PatchHunk> patchHunks =
                List.of(new EditorTool.PatchHunk(3, 3, "line3", "modified line3"));

        String result = getApplyPatches(originalContent, patchHunks);

        assertEquals("line1\nline2\nmodified line3\nline4", result);
    }

    public void testApplyPatches_EmptyPatchHunksList() {
        String originalContent = "line1\nline2\nline3";
        List<EditorTool.PatchHunk> patchHunks = List.of();

        String result = getApplyPatches(originalContent, patchHunks);

        assertEquals(originalContent, result);
    }

    public void testApplyPatches_ComplexScenario() {
        String originalContent =
                """
                public class Test {
                    private String field;
                   \s
                    public void method() {
                        System.out.println("old");
                    }
                }""";

        List<EditorTool.PatchHunk> patchHunks =
                Arrays.asList(
                        new EditorTool.PatchHunk(
                                2,
                                2,
                                "    private String field;",
                                "    private String field;\n    private int count;"),
                        new EditorTool.PatchHunk(
                                5,
                                5,
                                "        System.out.println(\"old\");",
                                "        System.out.println(\"new\");\n        count++;"));

        String result = getApplyPatches(originalContent, patchHunks);

        String expected =
                """
                public class Test {
                    private String field;
                    private int count;
                   \s
                    public void method() {
                        System.out.println("new");
                        count++;
                    }
                }""";

        assertEquals(expected, result);
    }

    private @NotNull String getApplyPatches(
            String originalContent, List<EditorTool.PatchHunk> patchHunks) {
        return editorTool.applyPatches(originalContent, new ArrayList<>(patchHunks));
    }
}
