package com.github.damiano1996.jetbrains.incoder.language.model.client.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ListFilesTool {

    @Tool("List all file paths in the folder and its subfolders")
    public List<String> listFiles(@P("Folder path") String folderPath) {
        log.info("Tool called, looking for files in folder: %s".formatted(folderPath));

        List<String> filePaths = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            listFilesRecursive(folder, filePaths);
        } else {
            filePaths.add("Invalid directory: " + folderPath);
        }
        return filePaths;
    }

    private void listFilesRecursive(@NotNull File dir, List<String> result) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                // listFilesRecursive(file, result);
            } else {
                log.debug("Adding new file to the list.");
                result.add(file.getAbsolutePath());
            }
        }
    }
}
