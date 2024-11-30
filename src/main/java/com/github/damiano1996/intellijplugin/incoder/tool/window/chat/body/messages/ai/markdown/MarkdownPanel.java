package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown;

import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.StreamWriter;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.CodeMarkdownBlock;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.MarkdownBlock;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.TextMarkdownBlock;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.actions.CreateCodeAction;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.actions.MergeAction;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class MarkdownPanel extends JPanel implements StreamWriter {

    public static final String MARKDOWN_CODE_BLOCK_DELIMITER = "```";

    private final Project project;
    private final List<MarkdownBlock> markdownBlocks;

    private boolean isWritingACodeBlock = false;
    private boolean nextIsLanguage = false;

    private String fullText = "";

    public MarkdownPanel(Project project) {
        this.project = project;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setFocusable(false);

        markdownBlocks = new LinkedList<>();

        addMarkdownEditorPane();
    }

    public @NotNull JComponent getActionToolbarComponent(CodeMarkdownBlock codeBlock) {
        var actionGroup = new DefaultActionGroup("Coding Group", true);

        actionGroup.add(new MergeAction(codeBlock));
        actionGroup.add(new CreateCodeAction(codeBlock));

        var actionToolbar =
                ActionManager.getInstance()
                        .createActionToolbar("CodeBlockToolbar", actionGroup, true);
        actionToolbar.setTargetComponent(codeBlock.getComponent());

        actionToolbar.setMiniMode(false);
        return actionToolbar.getComponent();
    }

    @Override
    public void write(@NotNull String token) {
        log.debug("Token received: {}", token);

        fullText += token;

        if (fullText.trim().endsWith(MARKDOWN_CODE_BLOCK_DELIMITER)) {
            nextIsLanguage = !isWritingACodeBlock;

            isWritingACodeBlock = !isWritingACodeBlock;

            if (!isWritingACodeBlock) {

                if (!fullText.endsWith(MARKDOWN_CODE_BLOCK_DELIMITER)) {
                    // There are tokenizers that split the code block delimiter in two sub-tokens.
                    // E.g. `` and `\n\n
                    // If the trimmed version of the fullText differs from the raw one,
                    // we must undo the last write to clean the code block.
                    markdownBlocks.get(markdownBlocks.size() - 1).undoLastWrite();
                }

                addMarkdownEditorPane();
            }

        } else if (nextIsLanguage) {

            var language = CodeMarkdownBlock.getLanguage(token);
            addCodeEditorPanel(language);
            nextIsLanguage = false;

        } else {
            markdownBlocks.get(markdownBlocks.size() - 1).write(token);
        }
    }

    @Override
    public void undoLastWrite() {
        throw new NotImplementedException();
    }

    @Override
    public String getFullText() {
        return fullText;
    }

    private void addCodeEditorPanel(Language language) {
        ApplicationManager.getApplication()
                .invokeAndWait(
                        () -> {
                            var codeMarkdownBlock = new CodeMarkdownBlock(project, language);
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
