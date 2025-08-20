// ChatSettings.java
package com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic.AnthropicParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.ollama.OllamaParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.openai.OpenAiParameters;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.XCollection;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
@Getter
@Service(Service.Level.APP)
@State(
        name = "ChatSettings",
        storages = {@Storage("InCoderSettings.xml")})
public final class ChatSettings implements PersistentStateComponent<ChatSettings.State> {

    @NotNull private State state = new State();

    public static ChatSettings getInstance() {
        return ApplicationManager.getApplication().getService(ChatSettings.class);
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    @ToString
    public static class State {
        // === LLM defaults già esistenti ======================================
        @XCollection(
                elementTypes = {
                        AnthropicParameters.class,
                        OllamaParameters.class,
                        OpenAiParameters.class
                },
                style = XCollection.Style.v2)
        public List<LanguageModelParameters> defaultLanguageModelParameters = new ArrayList<>();
        public int maxMessages = 20;
        public String systemMessageInstructions = loadDefaultSystemPrompt();

        // === Tools built-in: granularità =====================================
        public boolean enableFileTool = true;
        public boolean enableEditorTool = true;
        public boolean enableCommandLineTool = false;

        // === MCP: granularità e multi-config =================================
        public boolean enableMcp = true;

        @XCollection(elementTypes = {McpConfig.class}, style = XCollection.Style.v2)
        public List<McpConfig> mcpConfigs = new ArrayList<>();

        // Default system prompt loader
        public static @NotNull String loadDefaultSystemPrompt() {
            try (InputStream inputStream =
                         State.class.getClassLoader().getResourceAsStream("prompts/system_prompt.txt")) {
                if (inputStream != null) {
                    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                }
            } catch (IOException e) {
                log.error("Unable to read system prompt.", e);
            }
            return "";
        }

        public LanguageModelParameters getDefaultLanguageModelParameters() {
            return defaultLanguageModelParameters.isEmpty()
                    ? null
                    : defaultLanguageModelParameters.get(0);
        }

        public void setDefaultLanguageModelParameters(LanguageModelParameters parameters) {
            defaultLanguageModelParameters.clear();
            if (parameters != null) {
                defaultLanguageModelParameters.add(parameters);
            }
        }
    }

    // ===== Beans serializzabili per MCP =====================================

    @ToString
    public static class McpConfig {
        /** Identificatore/chiave usata anche come clientName in MCP */
        public String key = "memory";
        /** Comando completo in lista (es. ["npx","-y","@modelcontextprotocol/server-memory"]) */
        @XCollection(style = XCollection.Style.v2)
        public List<String> command = new ArrayList<>();
        /** Variabili d’ambiente come lista K/V serializzabile */
        @XCollection(elementTypes = {EnvVar.class}, style = XCollection.Style.v2)
        public List<EnvVar> env = new ArrayList<>();
        /** Abilitato/visibile al runtime */
        public boolean enabled = true;
        /** Loggare traffico MCP */
        public boolean logEvents = false;

        public static McpConfig memoryPreset() {
            McpConfig c = new McpConfig();
            c.key = "memory";
            c.command = List.of("npx", "-y", "@modelcontextprotocol/server-memory");
            c.env = new ArrayList<>();
            c.enabled = true;
            c.logEvents = true;
            return c;
        }
    }

    @ToString
    public static class EnvVar {
        public String key;
        public String value;
        public EnvVar() {}
        public EnvVar(String key, String value) { this.key = key; this.value = value; }
    }
}
