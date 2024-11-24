package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown;

import com.github.damiano1996.intellijplugin.incoder.generation.CodeGenerationService;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.StreamWriter;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.MarkdownBlock;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.TextMarkdownBlock;
import com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages.ai.markdown.blocks.CodeMarkdownBlock;
import com.intellij.icons.AllIcons;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class MarkdownPanel extends JPanel implements StreamWriter {

    public static final String MARKDOWN_CODE_BLOCK_DELIMITER = "```";

    private final List<MarkdownBlock> markdownBlocks;

    @Setter
    @Nullable
    private Project project;
    private boolean isWritingACodeBlock = false;
    private boolean nextIsLanguage = false;


    public MarkdownPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setFocusable(false);

        markdownBlocks = new ArrayList<>();

        addMarkdownEditorPane();
    }


    public @NotNull JComponent getJToolBarComponent(CodeMarkdownBlock codeBlock) {
        JToolBar toolBar = new JToolBar();

        // Create the "Merge..." button
        JButton mergeButton = new JButton("Merge...", AllIcons.Vcs.Merge);
        mergeButton.setToolTipText("Merge selected changes");

        mergeButton.addActionListener((ActionEvent e) -> {
            assert project != null;
            CodeGenerationService.showDiff(
                        project,
                        codeBlock.getFullText(),
                        Objects.requireNonNull(
                                FileEditorManager.getInstance(project)
                                        .getSelectedTextEditor()
                        )
                );

        });

        toolBar.add(mergeButton);

        return toolBar;
    }


    public @NotNull JComponent getActionToolbarComponent(CodeMarkdownBlock codeBlock) {
        var actionGroup = new DefaultActionGroup();

        actionGroup.add(new AnAction("Merge...", "Merge selected changes", AllIcons.Vcs.Merge) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                Project project = e.getProject();
                if (project != null) {
                    CodeGenerationService.showDiff(
                            project,
                            codeBlock.getFullText(),
                            Objects.requireNonNull(
                                    FileEditorManager.getInstance(project)
                                            .getSelectedTextEditor()
                            )
                    );
                }
            }
        });

        var actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actionGroup, true);
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

            var fileType = FileTypeManager.getInstance().getFileTypeByExtension(language.getID());

            addCodeEditorPanel(fileType);

            nextIsLanguage = false;
        } else {

            markdownBlocks.get(markdownBlocks.size() - 1).write(token);

        }
    }

    @Override
    public String getFullText() {
        return markdownBlocks.stream().map(StreamWriter::getFullText).collect(Collectors.joining("\n"));
    }

    public static @NotNull Language getLanguage(String languageName) {
        var languages = Language.getRegisteredLanguages();

        for (Language language : languages) {
            if (language.getID().equalsIgnoreCase(languageName)){
                return language;
            }
        }

        throw new IllegalArgumentException("'%s' is not a valid coding language.".formatted(languageName));
    }

    private void addCodeEditorPanel(FileType fileType) {
        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            var codeMarkdownBlock = new CodeMarkdownBlock(project, fileType, "");
                            addMarkdownBlock(codeMarkdownBlock);

                            add(getJToolBarComponent(codeMarkdownBlock));
                            add(getActionToolbarComponent(codeMarkdownBlock));

                            var button = new JButton("Hello");
                            button.setFocusable(true);
                            add(button);
                        });
    }

    private void addMarkdownEditorPane() {
        ApplicationManager.getApplication()
                .invokeLater(
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
