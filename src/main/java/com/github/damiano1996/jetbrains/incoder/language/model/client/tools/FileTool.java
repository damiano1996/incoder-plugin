package com.github.damiano1996.jetbrains.incoder.language.model.client.tools;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@AllArgsConstructor
public class FileTool {

    private final Project project;

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

        log.info("Returning {} file/folder paths for directory: {}", filePaths.size(), folderPath);
        return filePaths;
    }

    @Tool("Read the content of a file given its path")
    public String readFile(@P("File path") String filePath) {
        log.info("Tool called, reading file content from: %s".formatted(filePath));

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                String errorMsg = "File does not exist: " + filePath;
                log.warn("Returning error: {}", errorMsg);
                return errorMsg;
            }
            if (file.isDirectory()) {
                String errorMsg = "Path is a directory, not a file: " + filePath;
                log.warn("Returning error: {}", errorMsg);
                return errorMsg;
            }

            String content = Files.readString(Paths.get(filePath));
            log.debug("Successfully read file content, length: {} characters", content.length());
            log.info(
                    "Returning file content for: {}, length: {} characters",
                    filePath,
                    content.length());
            return content;
        } catch (IOException e) {
            log.error("Error reading file: {}", filePath, e);
            return "Error reading file: %s".formatted(e.getMessage());
        }
    }

    @Tool(
            "Create a file at the given path with the specified content. Only if the file does not"
                    + " exist yet.")
    public String createFile(@P("File path") String filePath, @P("File content") String content) {
        log.info("Tool called, creating file at: {}", filePath);

        try {
            File file = new File(filePath);

            createPath(filePath, file);

            // Check if file already exists
            if (file.exists()) {
                String errorMsg = "File already exists: %s".formatted(filePath);
                log.warn("Returning error: {}", errorMsg);
                return errorMsg;
            }

            Files.writeString(Paths.get(filePath), content);

            VirtualFile projectRoot = project.getBaseDir();
            projectRoot.refresh(true, true);

            String successMsg = "File created successfully: %s".formatted(filePath);
            log.info("Successfully created file: {}", filePath);
            return successMsg;
        } catch (IOException e) {
            log.error("Error creating file: {}", filePath, e);
            return "Error creating file: %s".formatted(e.getMessage());
        }
    }

    private static void createPath(String filePath, @NotNull File file) {
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean dirsCreated = parentDir.mkdirs();
            if (dirsCreated) {
                log.debug("Created parent directories for: {}", filePath);
            }
        }
    }
}
