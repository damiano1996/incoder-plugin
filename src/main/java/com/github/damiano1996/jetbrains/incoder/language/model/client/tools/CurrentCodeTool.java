package com.github.damiano1996.jetbrains.incoder.language.model.client.tools;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class CurrentCodeTool {

    private Project project;

    @Tool("Get the code that the user is currently viewing in the IDE")
    public String getCurrentCode() {
        Editor editor =
                FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            return editor.getDocument().getText();
        }else
        {
            return "Error: unable to read the content of the document that the user is reading.";
        }
    }

    @Tool("Get the file path of the code that the user is currently viewing")
    public String getCurrentFilePath() {
        Editor editor =
                FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            return editor.getVirtualFile().getPath();
        }else
        {
            return "Error: unable to get the path of the file the user is reading.";
        }
    }
}
