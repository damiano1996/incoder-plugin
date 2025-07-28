package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown;

import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.StreamWriter;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.MarkdownBlock;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.text.TextMarkdownBlock;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class MarkdownPanel extends JPanel implements StreamWriter {

    public static final String MARKDOWN_CODE_BLOCK_DELIMITER = "```";
    public static final String MARKDOWN_CODE_BLOCK_START_REGEX = "(?m)^```(?:[a-zA-Z0-9]+)?\\n";
    public static final String MARKDOWN_CODE_BLOCK_END_REGEX = "(?m)^```";

    @Getter private final Project project;

    @Getter(AccessLevel.PROTECTED)
    private final List<MarkdownBlock> markdownBlocks;

    public MarkdownPanel(Project project) {
        this.project = project;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setFocusable(false);

        markdownBlocks = new LinkedList<>();

        log.debug("Start with a markdown editor pane");
        next(new TextMarkdownBlock(this));
    }

    @Override
    public void write(@NotNull String token) {
        log.debug("Token received: {}", token);
        markdownBlocks.get(markdownBlocks.size() - 1).write(token);
    }

    @Override
    public String getText() {
        return markdownBlocks.stream().map(StreamWriter::getText).collect(Collectors.joining("\n"));
    }

    @Override
    public void streamClosed() {
        markdownBlocks.get(markdownBlocks.size() - 1).streamClosed();
    }

    public void next(MarkdownBlock markdownBlock) {
        ApplicationManager.getApplication()
                .invokeAndWait(
                        () -> {
                            markdownBlocks.add(markdownBlock);
                            add(markdownBlock.getMainPanel());
                        });
    }
}
