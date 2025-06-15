package com.github.damiano1996.jetbrains.incoder.language.model.client.tools;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class EditorTool {

    private Project project;

    @Tool("Get the content of the file the user is watching")
    public String getFileContent() {
        log.debug("Getting current code from active editor");
        Editor editor =
                FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            String code = editor.getDocument().getText();
            log.debug("Successfully retrieved current code, length: {} characters", code.length());
            return code;
        } else {
            log.warn("No active editor found, unable to retrieve current code");
            return "Error: unable to read the content of the document that the user is reading.";
        }
    }

    @Tool("Get the path of the file the user is watching")
    public String getFilePath() {
        try {
            log.debug("Getting current file path from active editor");
            Editor editor =
                    FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor != null) {
                String filePath = editor.getVirtualFile().getPath();
                log.debug("Successfully retrieved current file path: {}", filePath);
                return filePath;
            } else {
                log.warn("No active editor found, unable to retrieve current file path");
                return "Error: unable to get the path of the file the user is reading.";
            }
        } catch (Exception e) {
            return "Error: %s".formatted(e.getMessage());
        }
    }
}
