package com.github.damiano1996.jetbrains.incoder.formatting;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for formatting output text for display. Provides methods to truncate long text
 * outputs to keep UI elements readable.
 */
@UtilityClass
public class OutputFormatter {

    private static final int DEFAULT_MAX_OUTPUT_LINES = 100;
    private static final int DEFAULT_HEAD_LINES = 50;
    private static final int DEFAULT_TAIL_LINES = 50;

    /**
     * Truncates output to a reasonable length with default settings.
     *
     * @param output the output to truncate
     * @return the truncated output
     */
    @NotNull
    public String truncateOutput(@NotNull String output) {
        return truncateOutput(
                output, DEFAULT_MAX_OUTPUT_LINES, DEFAULT_HEAD_LINES, DEFAULT_TAIL_LINES);
    }

    /**
     * Truncates output if it exceeds the maximum number of lines. Keeps the first N lines and last
     * N lines, with a truncation indicator in the middle.
     *
     * @param output the output to truncate
     * @param maxOutputLines the maximum number of lines before truncation
     * @param headLines the number of lines to keep from the beginning
     * @param tailLines the number of lines to keep from the end
     * @return the truncated output
     */
    @NotNull
    public String truncateOutput(
            @NotNull String output, int maxOutputLines, int headLines, int tailLines) {

        String[] lines = output.split("\n");

        if (lines.length <= maxOutputLines) {
            return output;
        }

        StringBuilder result = new StringBuilder();

        // Add first N lines
        for (int i = 0; i < headLines; i++) {
            result.append(lines[i]).append("\n");
        }

        // Add truncation indicator
        result.append("\n... [OUTPUT TRUNCATED - ")
                .append(lines.length - headLines - tailLines)
                .append(" lines omitted] ...\n\n");

        // Add last N lines
        for (int i = lines.length - tailLines; i < lines.length; i++) {
            result.append(lines[i]).append("\n");
        }

        return result.toString();
    }
}
