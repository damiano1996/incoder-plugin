package com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic;

import static com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic.AnthropicLanguageModelServer.ANTHROPIC;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.settings.ui.BaseProviderUIStrategy;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;

public class AnthropicUIStrategy extends BaseProviderUIStrategy {
    private JSpinner topK;
    private JTextField version;
    private JCheckBox cacheSystem;
    private JCheckBox cacheTools;

    public AnthropicUIStrategy() {
        super(ANTHROPIC);
    }

    @Override
    public JPanel buildPanel() {
        if (panel != null) return panel;
        panel =
                FormBuilder.createFormBuilder()
                        .addLabeledComponent(
                                new JBLabel("TopK:"),
                                (topK = new JSpinner(new SpinnerNumberModel(40, 1, 200, 1))),
                                0,
                                false)
                        .addLabeledComponent(
                                new JBLabel("Version:"),
                                (version = new JTextField("2023-06-01")),
                                0,
                                false)
                        .addComponent((cacheSystem = new JCheckBox("Cache system messages")))
                        .addComponent((cacheTools = new JCheckBox("Cache tools")))
                        .getPanel();
        return panel;
    }

    @Override
    public void applyDefaults(LanguageModelParameters defaults) {
        if (defaults instanceof AnthropicParameters a) {
            topK.setValue(nz(a.topK, 40));
            version.setText(nz(a.version, "2023-06-01"));
            cacheSystem.setSelected(Boolean.TRUE.equals(a.cacheSystemMessages));
            cacheTools.setSelected(Boolean.TRUE.equals(a.cacheTools));
        }
    }

    @Override
    public LanguageModelParameters.LanguageModelParametersBuilder<?, ?> collect(
            LanguageModelParameters.LanguageModelParametersBuilder<?, ?> builder) {
        return ((AnthropicParameters.AnthropicParametersBuilder<?, ?>) builder)
                .serverName(ANTHROPIC)
                .topK((Integer) topK.getValue())
                .version(version.getText())
                .cacheSystemMessages(cacheSystem.isSelected())
                .cacheTools(cacheTools.isSelected());
    }

    @Override
    public LanguageModelParameters.LanguageModelParametersBuilder<?, ?> getBuilder() {
        return AnthropicParameters.builder();
    }
}
