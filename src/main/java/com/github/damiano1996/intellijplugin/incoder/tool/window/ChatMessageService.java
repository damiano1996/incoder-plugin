package com.github.damiano1996.intellijplugin.incoder.tool.window;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Getter
@Service(Service.Level.PROJECT)
@Slf4j
public final class ChatMessageService {

    private final List<ChatMessage> chatMessageList = new LinkedList<>();

    public static ChatMessageService getInstance(@NotNull Project project) {
        return project.getService(ChatMessageService.class);
    }
}
