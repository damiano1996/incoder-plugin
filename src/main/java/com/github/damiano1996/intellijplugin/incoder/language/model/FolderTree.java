package com.github.damiano1996.intellijplugin.incoder.language.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class FolderTree {

    public static @NotNull String getFolderPaths(@NotNull Path folder) {
        StringBuilder paths = new StringBuilder();
        paths.append(folder.toAbsolutePath()).append("\n");
        try (var stream = Files.list(folder)) {
            for (Path path :
                    stream.filter(Files::isDirectory)
                            .filter(p -> !p.getFileName().toString().startsWith("."))
                            .toList()) {

                paths.append(getFolderPaths(path));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Unable to list folders from the given path: %s".formatted(folder));
        }

        return paths.toString();
    }
}
