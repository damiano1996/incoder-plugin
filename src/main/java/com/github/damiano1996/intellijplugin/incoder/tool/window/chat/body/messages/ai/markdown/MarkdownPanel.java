package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown;

import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.StreamWriter;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.CodeMarkdownBlock;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.MarkdownBlock;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.TextMarkdownBlock;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.actions.CreateCodeAction;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.actions.MergeAction;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class MarkdownPanel extends JPanel implements StreamWriter {

    public static final String MARKDOWN_CODE_BLOCK_DELIMITER = "```";

    private final List<MarkdownBlock> markdownBlocks;

    @Setter @Nullable private Project project;
    private boolean isWritingACodeBlock = false;
    private boolean nextIsLanguage = false;

    public MarkdownPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setFocusable(false);

        markdownBlocks = new LinkedList<>();

        addMarkdownEditorPane();
    }

    public static @NotNull Language getLanguage(String languageName) {
        var languages = Language.getRegisteredLanguages();

        for (Language language : languages) {
            if (language.getID().equalsIgnoreCase(languageName)) {
                return language;
            }
        }

        log.debug("Unable to infer the language from the language name");
        return Language.ANY;
    }

    public @NotNull JComponent getActionToolbarComponent(CodeMarkdownBlock codeBlock) {
        var actionGroup = new DefaultActionGroup("Coding Group", true);

        actionGroup.add(new MergeAction(codeBlock));
        actionGroup.add(new CreateCodeAction(codeBlock));

        var actionToolbar =
                ActionManager.getInstance()
                        .createActionToolbar("CodeBlockToolbar", actionGroup, true);
        actionToolbar.setMiniMode(false);
        return actionToolbar.getComponent();
    }

    @Override
    public void write(@NotNull String token) {
        if (project == null) return;

        if (token.startsWith(MARKDOWN_CODE_BLOCK_DELIMITER)) {
            nextIsLanguage = !isWritingACodeBlock;

            isWritingACodeBlock = !isWritingACodeBlock;

            if (!isWritingACodeBlock) {
                addMarkdownEditorPane();
            }

        } else if (nextIsLanguage) {

            var language = getLanguage(token);
            log.info("Code block language: {}", language.getID());

            var fileType = FileTypeManager.getInstance().findFileTypeByLanguage(language);

            addCodeEditorPanel(fileType);

            nextIsLanguage = false;
        } else {

            markdownBlocks.get(markdownBlocks.size() - 1).write(token);
        }
    }

    @Override
    public String getFullText() {
        return markdownBlocks.stream()
                .map(StreamWriter::getFullText)
                .collect(Collectors.joining("\n"));
    }

    private void addCodeEditorPanel(FileType fileType) {
        ApplicationManager.getApplication()
                .invokeAndWait(
                        () -> {
                            var codeMarkdownBlock = new CodeMarkdownBlock(project, fileType, "");
                            addMarkdownBlock(codeMarkdownBlock);

                            add(getActionToolbarComponent(codeMarkdownBlock));
                        });
    }

    private void addMarkdownEditorPane() {
        ApplicationManager.getApplication()
                .invokeAndWait(
                        () -> {
                            var textMarkdownBlock = new TextMarkdownBlock();
                            addMarkdownBlock(textMarkdownBlock);
                        });
    }

    private void addMarkdownBlock(MarkdownBlock markdownBlock) {
        markdownBlocks.add(markdownBlock);
        add(markdownBlock.getComponent());
    }
}
