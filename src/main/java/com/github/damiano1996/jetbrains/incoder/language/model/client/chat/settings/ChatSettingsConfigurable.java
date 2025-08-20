package com.github.damiano1996.jetbrains.incoder.language.model.client.chat.settings;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public final class ChatSettingsConfigurable implements Configurable {

    private ChatSettingsComponent chatSettingsComponent;

    public ChatSettingsConfigurable() {
        chatSettingsComponent = new ChatSettingsComponent();
    }

    private static ChatSettings.@NotNull State getState() {
        return ChatSettings.getInstance().getState();
    }

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "Chat";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return chatSettingsComponent.getMainPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return chatSettingsComponent.getMainPanel();
    }
    // ChatSettingsConfigurable.java (solo i metodi aggiornati)
    @Override
    public boolean isModified() {
        var state = getState();
        boolean modified =
                !chatSettingsComponent.getMaxMessages().getValue().equals(state.maxMessages)
                || !chatSettingsComponent.getSystemMessageInstructionsField().getText().equals(state.systemMessageInstructions)
                || chatSettingsComponent.getEnableFileTool().isSelected() != state.enableFileTool
                || chatSettingsComponent.getEnableEditorTool().isSelected() != state.enableEditorTool
                || chatSettingsComponent.getEnableCommandLineTool().isSelected() != state.enableCommandLineTool
                || chatSettingsComponent.getEnableMcp().isSelected() != state.enableMcp;

        // confronta lista MCP
        var uiList = chatSettingsComponent.getMcpConfigs();
        var stList = state.mcpConfigs;
        if (uiList.size() != stList.size()) return true;
        for (int i = 0; i < uiList.size(); i++) {
            if (!uiList.get(i).toString().equals(stList.get(i).toString())) return true;
        }
        return modified;
    }

    @Override
    public void apply() {
        var state = getState();
        state.maxMessages = (int) chatSettingsComponent.getMaxMessages().getValue();
        state.systemMessageInstructions = chatSettingsComponent.getSystemMessageInstructionsField().getText();

        state.enableFileTool = chatSettingsComponent.getEnableFileTool().isSelected();
        state.enableEditorTool = chatSettingsComponent.getEnableEditorTool().isSelected();
        state.enableCommandLineTool = chatSettingsComponent.getEnableCommandLineTool().isSelected();

        state.enableMcp = chatSettingsComponent.getEnableMcp().isSelected();
        state.mcpConfigs = new ArrayList<>(chatSettingsComponent.getMcpConfigs());
    }

    @Override
    public void reset() {
        var state = getState();
        chatSettingsComponent.getMaxMessages().setValue(state.maxMessages);
        chatSettingsComponent.getSystemMessageInstructionsField().setText(state.systemMessageInstructions);

        chatSettingsComponent.getEnableFileTool().setSelected(state.enableFileTool);
        chatSettingsComponent.getEnableEditorTool().setSelected(state.enableEditorTool);
        chatSettingsComponent.getEnableCommandLineTool().setSelected(state.enableCommandLineTool);

        chatSettingsComponent.getEnableMcp().setSelected(state.enableMcp);

        // se non c'è nulla, predisponi un preset "memory"
        if (state.mcpConfigs == null || state.mcpConfigs.isEmpty()) {
            state.mcpConfigs = new ArrayList<>();
            state.mcpConfigs.add(ChatSettings.McpConfig.memoryPreset());
        }
        chatSettingsComponent.setMcpConfigs(state.mcpConfigs);
    }


    @Override
    public void disposeUIResources() {
        chatSettingsComponent = null;
    }
}
