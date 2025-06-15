package com.github.damiano1996.jetbrains.incoder.language.model.client;

import com.github.damiano1996.jetbrains.incoder.completion.CodeCompletionContext;
import com.github.damiano1996.jetbrains.incoder.language.model.LanguageModelException;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.ChatCodingAssistant;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings.ChatSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.InlineCodingAssistant;
import com.github.damiano1996.jetbrains.incoder.language.model.client.inline.settings.InlineSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.client.tools.EditorTool;
import com.github.damiano1996.jetbrains.incoder.language.model.client.tools.FileTool;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class LanguageModelClientImpl implements LanguageModelClient {

    private final Project project;
    private final ChatLanguageModel chatLanguageModel;

    private final ChatCodingAssistant chatCodingAssistant;
    private final InlineCodingAssistant inlineCodingAssistant;

    private final String projectName;
    private final String projectPath;
    private final String ideInfo;
    private final String userTimezone;

    public LanguageModelClientImpl(
            @NotNull Project project,
            ChatLanguageModel chatLanguageModel,
            StreamingChatLanguageModel streamingChatLanguageModel) {
        this.project = project;
        this.chatLanguageModel = chatLanguageModel;

        this.projectName = project.getName();
        this.projectPath = getProjectPath();
        this.ideInfo = getIdeInfo();
        this.userTimezone = getUserTimezone();

        chatCodingAssistant =
                AiServices.builder(ChatCodingAssistant.class)
                        .streamingChatLanguageModel(streamingChatLanguageModel)
                        .chatLanguageModel(chatLanguageModel)
                        .chatMemoryProvider(
                                memoryId ->
                                        MessageWindowChatMemory.withMaxMessages(
                                                ChatSettings.getInstance().getState().maxMessages))
                        .tools(new FileTool(), new EditorTool(this.project))
                        .build();

        inlineCodingAssistant =
                AiServices.builder(InlineCodingAssistant.class)
                        .streamingChatLanguageModel(streamingChatLanguageModel)
                        .chatLanguageModel(chatLanguageModel)
                        .build();
    }

    @Override
    public String complete(@NotNull CodeCompletionContext codeCompletionContext) {
        return inlineCodingAssistant.complete(
                InlineSettings.getInstance().getState().systemMessageInstructions,
                codeCompletionContext.leftContext(),
                codeCompletionContext.rightContext());
    }

    @Override
    public TokenStream chat(int memoryId, String prompt) {
        String currentDate = getCurrentDate();
        String currentFile = getCurrentFile();
        String programmingLanguage = getProgrammingLanguage();
        String systemMessageInstructions =
                ChatSettings.getInstance().getState().systemMessageInstructions;

        log.info("Chat method called with parameters:");
        log.info("memoryId: {}", memoryId);
        log.info("prompt: {}", prompt);
        log.info("systemMessageInstructions: {}", systemMessageInstructions);
        log.info("currentDate: {}", currentDate);
        log.info("projectName: {}", projectName);
        log.info("projectPath: {}", projectPath);
        log.info("currentFile: {}", currentFile);
        log.info("programmingLanguage: {}", programmingLanguage);
        log.info("ideInfo: {}", ideInfo);
        log.info("userTimezone: {}", userTimezone);

        return chatCodingAssistant.chat(
                memoryId,
                systemMessageInstructions,
                currentDate,
                projectName,
                projectPath,
                currentFile,
                programmingLanguage,
                ideInfo,
                userTimezone,
                prompt);
    }

    private @NotNull String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String getProjectPath() {
        return project.getBasePath() != null ? project.getBasePath() : "Unknown";
    }

    private @NotNull String getCurrentFile() {
        VirtualFile[] selectedFiles = FileEditorManager.getInstance(project).getSelectedFiles();
        return selectedFiles.length > 0 ? selectedFiles[0].getName() : "No file selected";
    }

    private @NotNull String getProgrammingLanguage() {
        VirtualFile[] selectedFiles = FileEditorManager.getInstance(project).getSelectedFiles();
        if (selectedFiles.length > 0) {
            String extension = selectedFiles[0].getExtension();
            return extension != null ? extension : "Unknown";
        }
        return "Unknown";
    }

    private @NotNull String getIdeInfo() {
        ApplicationInfo appInfo = ApplicationInfo.getInstance();
        return appInfo.getFullApplicationName() + " " + appInfo.getFullVersion();
    }

    private String getUserTimezone() {
        return ZoneId.systemDefault().toString();
    }

    @Override
    public void checkServerConnection() throws LanguageModelException {
        try {
            chatLanguageModel.chat("Hello!");
        } catch (Exception e) {
            throw new LanguageModelException(e);
        }
    }
}
