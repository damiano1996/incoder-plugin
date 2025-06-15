package com.github.damiano1996.jetbrains.incoder.language.model.client.tools;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class FileTool {

    @Tool("List all file and folder paths in the given folder")
    public List<String> listFileAndFolderPaths(@P("Folder path") String folderPath) {
        log.info("Tool called, looking for files and folders in folder: %s".formatted(folderPath));

        List<String> filePaths = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        log.debug("Adding new folder to the list.");
                        filePaths.add(file.getAbsolutePath() + "/");
                    } else {
                        log.debug("Adding new file to the list.");
                        filePaths.add(file.getAbsolutePath());
                    }
                }
            }
        } else {
            filePaths.add("Invalid directory: " + folderPath);
        }
        return filePaths;
    }

    @Tool("Read the content of a file given its path")
    public String readFile(@P("File path") String filePath) {
        log.info("Tool called, reading file content from: %s".formatted(filePath));

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return "File does not exist: " + filePath;
            }
            if (file.isDirectory()) {
                return "Path is a directory, not a file: " + filePath;
            }

            String content = Files.readString(Paths.get(filePath));
            log.debug("Successfully read file content, length: {} characters", content.length());
            return content;
        } catch (IOException e) {
            log.error("Error reading file: {}", filePath, e);
            return "Error reading file: " + e.getMessage();
        }
    }

}
