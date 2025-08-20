package com.github.damiano1996.jetbrains.incoder.language.model.client.chat;

import com.github.damiano1996.jetbrains.incoder.language.model.client.BaseLanguageModelClient;
import com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings.ChatSettings;
import com.github.damiano1996.jetbrains.incoder.language.model.client.tools.EditorTool;
import com.github.damiano1996.jetbrains.incoder.language.model.client.tools.FileTool;
import com.github.damiano1996.jetbrains.incoder.language.model.client.tools.commandline.CommandLineTool;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import dev.langchain4j.service.tool.ToolProvider;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ChatLanguageModelClientImpl extends BaseLanguageModelClient
        implements ChatLanguageModelClient {

    public static final String UNKNOWN = "Unknown";

    private final ChatCodingAssistant chatCodingAssistant;

    private final String projectName;
    private final String projectPath;
    private final String ideInfo;
    private final String userTimezone;

    public ChatLanguageModelClientImpl(
            LanguageModelParameters parameters,
            StreamingChatLanguageModel streamingChatLanguageModel) {
        super(parameters);

        Project project = ProjectUtil.getActiveProject();

        this.projectName = getProjectName(project);
        this.projectPath = getProjectPath(project);
        this.ideInfo = getIdeInfo();
        this.userTimezone = getUserTimezone();

        chatCodingAssistant = getChatCodingAssistant(streamingChatLanguageModel);
    }

    private static @NotNull String getProjectName(Project project) {
        if (project == null) return UNKNOWN;
        return project.getName();
    }

    // ChatLanguageModelClientImpl.java (solo metodo getChatCodingAssistant)
    private ChatCodingAssistant getChatCodingAssistant(
            StreamingChatLanguageModel streamingChatLanguageModel) {
        final ChatCodingAssistant chatCodingAssistant;

        var state = ChatSettings.getInstance().getState();

        AiServices<ChatCodingAssistant> aiAssistantBuilder =
                AiServices.builder(ChatCodingAssistant.class)
                        .streamingChatLanguageModel(streamingChatLanguageModel)
                        .chatMemoryProvider(
                                memoryId ->
                                        MessageWindowChatMemory.withMaxMessages(state.maxMessages));

        // === Built-in tools per flag ===========================================
        if (state.enableFileTool) aiAssistantBuilder.tools(new FileTool());
        if (state.enableEditorTool) aiAssistantBuilder.tools(new EditorTool());
        if (state.enableCommandLineTool) aiAssistantBuilder.tools(new CommandLineTool());

        // === MCP ================================================================
        if (state.enableMcp && state.mcpConfigs != null && !state.mcpConfigs.isEmpty()) {
            List<McpClient> clients = new java.util.ArrayList<>();
            for (var cfg : state.mcpConfigs) {
                if (!cfg.enabled) continue;
                List<String> cmd = cfg.command == null || cfg.command.isEmpty()
                        ? List.of("npx", "-y", "@modelcontextprotocol/server-memory")
                        : cfg.command;

                // build env map (+ default persistence se non fornita)
                java.util.Map<String, String> env = new java.util.HashMap<>();
                if (cfg.env != null) {
                    for (var ev : cfg.env) {
                        if (ev.key != null && !ev.key.isBlank()) {
                            env.put(ev.key, ev.value == null ? "" : ev.value);
                        }
                    }
                }
                // default persistence path per memory server (non invasivo)
                if (cmd.contains("@modelcontextprotocol/server-memory") &&
                    env.keySet().stream().noneMatch(k -> k.equals("MEMORY_FILE_PATH"))) {
                    String fallbackPath =
                            (projectPath != null && !UNKNOWN.equals(projectPath))
                                    ? projectPath + "/.incoder/memory.json"
                                    : System.getProperty("user.home") + "/.incoder/memory.json";
                    env.put("MEMORY_FILE_PATH", fallbackPath);
                }

                McpTransport transport = new StdioMcpTransport.Builder()
                        .command(cmd)
                        .environment(env)
                        .logEvents(cfg.logEvents)
                        .build();

                McpClient mcpClient = new DefaultMcpClient.Builder()
                        .clientName(cfg.key == null || cfg.key.isBlank() ? "mcp" : cfg.key)
                        .transport(transport)
                        .build();

                clients.add(mcpClient);
            }

            if (!clients.isEmpty()) {
                ToolProvider toolProvider = McpToolProvider.builder()
                        .mcpClients(clients)
                        .build();
                aiAssistantBuilder.toolProvider(toolProvider);
            }
        }

        chatCodingAssistant = aiAssistantBuilder.build();
        return chatCodingAssistant;
    }

    @Override
    public TokenStream chat(int memoryId, String prompt) {
        Project project = ProjectUtil.getActiveProject();

        String currentDate = getCurrentDate();
        String currentFile = getCurrentFile(project);
        String programmingLanguage = getProgrammingLanguage(project);
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

    private String getProjectPath(Project project) {
        if (project == null) return UNKNOWN;
        return project.getBasePath() != null ? project.getBasePath() : UNKNOWN;
    }

    private @NotNull String getCurrentFile(Project project) {
        if (project == null) return UNKNOWN;
        VirtualFile[] selectedFiles = FileEditorManager.getInstance(project).getSelectedFiles();
        return selectedFiles.length > 0 ? selectedFiles[0].getPath() : "No file selected";
    }

    private @NotNull String getProgrammingLanguage(Project project) {
        if (project == null) return UNKNOWN;

        VirtualFile[] selectedFiles = FileEditorManager.getInstance(project).getSelectedFiles();
        if (selectedFiles.length > 0) {
            String extension = selectedFiles[0].getExtension();
            return extension != null ? extension : UNKNOWN;
        }
        return UNKNOWN;
    }

    private @NotNull String getIdeInfo() {
        ApplicationInfo appInfo = ApplicationInfo.getInstance();
        return appInfo.getFullApplicationName() + " " + appInfo.getFullVersion();
    }

    private String getUserTimezone() {
        return ZoneId.systemDefault().toString();
    }
}
