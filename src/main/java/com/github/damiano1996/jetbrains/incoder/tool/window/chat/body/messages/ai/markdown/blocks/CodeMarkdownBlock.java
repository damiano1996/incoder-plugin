package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks;

import static com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.MarkdownPanel.MARKDOWN_CODE_BLOCK_END_REGEX;

import com.github.damiano1996.jetbrains.incoder.tool.window.ToolWindowColors;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.MarkdownPanel;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.actions.CopyClipboardCodeAction;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.actions.CreateCodeAction;
import com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks.actions.MergeAction;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.testFramework.LightVirtualFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class CodeMarkdownBlock extends JPanel implements MarkdownBlock {

    private final MarkdownPanel markdownPanel;

    @Getter(AccessLevel.PROTECTED)
    private Editor editor;
    private final Language language;

    public CodeMarkdownBlock(
            MarkdownPanel markdownPanel, String language, @NotNull CharSequence initCode) {
        this(markdownPanel, getLanguage(language), initCode);
    }

    public CodeMarkdownBlock(
            MarkdownPanel markdownPanel, Language language, CharSequence initCode) {
        this.markdownPanel = markdownPanel;
        this.language = language;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ToolWindowColors.AI_MESSAGE_BACKGROUND);
        setForeground(ToolWindowColors.AI_MESSAGE_FOREGROUND);
        setFocusable(false);

        initEditor(initCode);
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

    @Override
    public JComponent getMainPanel() {
        return this;
    }

    @Override
    public void write(@NotNull String token) {
        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            String currentCode = editor.getDocument().getText();
                            String updatedCode = currentCode + token;

                            updateEditor(updatedCode);

                            lookForTextBlock(updatedCode);
                        });
    }

    private void lookForTextBlock(String updatedCode) {
        if (language.getID().equals("Markdown")) {
            // TODO: this is a special case. It will require an improvement.
            return;
        }

        Matcher matcher = Pattern.compile(MARKDOWN_CODE_BLOCK_END_REGEX).matcher(updatedCode);

        int matchStartIndex = -1;

        while (matcher.find()) {
            matchStartIndex = matcher.start();
        }

        if (matchStartIndex != -1) {
            log.debug("Text block found at index: {}", matchStartIndex);

            var codeUntilText = updatedCode.substring(0, matchStartIndex);
            var text = updatedCode.substring(matchStartIndex);

            updateEditor(codeUntilText);

            markdownPanel.next(new TextMarkdownBlock(markdownPanel, text));
        }
    }

    private void updateEditor(String updatedCode) {
        ApplicationManager.getApplication()
                .runWriteAction(() -> editor.getDocument().setText(updatedCode));
    }

    private void initEditor(@NotNull CharSequence code) {
        var fileType = FileTypeManager.getInstance().findFileTypeByLanguage(language);
        var virtualFile = new LightVirtualFile("temp", fileType, code);
        var document = EditorFactory.getInstance().createDocument(code);

        ApplicationManager.getApplication()
                .invokeAndWait(
                        () -> {
                            editor =
                                    EditorFactory.getInstance()
                                            .createEditor(
                                                    document,
                                                    markdownPanel.getProject(),
                                                    virtualFile,
                                                    false,
                                                    EditorKind.PREVIEW);
                            ((EditorEx) editor).setViewer(true);

                            add(editor.getComponent());
                            add(getActionToolbarComponent());
                        });
    }

    @Override
    public String getFullText() {
        return editor.getDocument().getText();
    }

    private @NotNull JComponent getActionToolbarComponent() {
        var actionGroup = new DefaultActionGroup("Coding Group", true);

        actionGroup.add(new MergeAction(this));
        actionGroup.add(new CreateCodeAction(this));
        actionGroup.add(new CopyClipboardCodeAction(this));

        var actionToolbar =
                ActionManager.getInstance()
                        .createActionToolbar("CodeBlockToolbar", actionGroup, true);
        actionToolbar.setTargetComponent(this);

        actionToolbar.setMiniMode(false);
        return actionToolbar.getComponent();
    }
}
