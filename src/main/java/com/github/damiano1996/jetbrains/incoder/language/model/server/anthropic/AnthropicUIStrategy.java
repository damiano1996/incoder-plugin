package com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic;

import static com.github.damiano1996.jetbrains.incoder.language.model.server.anthropic.AnthropicLanguageModelServer.ANTHROPIC;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.settings.ui.BaseProviderUIStrategy;
import com.github.damiano1996.jetbrains.incoder.language.model.server.settings.ui.CommonModelParameters;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;

public class AnthropicUIStrategy extends BaseProviderUIStrategy {
    private JSpinner topK;
    private JTextField version;
    private JTextField beta;
    private JTextField thinkingType;
    private JSpinner thinkingBudget;
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
                        .addLabeledComponent(
                                new JBLabel("Beta:"), (beta = new JTextField()), 0, false)
                        .addLabeledComponent(
                                new JBLabel("ThinkingType:"),
                                (thinkingType = new JTextField()),
                                0,
                                false)
                        .addLabeledComponent(
                                new JBLabel("ThinkingBudgetTokens:"),
                                (thinkingBudget =
                                        new JSpinner(new SpinnerNumberModel(0, 0, 32000, 1))),
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
            beta.setText(nz(a.beta, ""));
            thinkingType.setText(nz(a.thinkingType, ""));
            thinkingBudget.setValue(nz(a.thinkingBudgetTokens, 0));
            cacheSystem.setSelected(Boolean.TRUE.equals(a.cacheSystemMessages));
            cacheTools.setSelected(Boolean.TRUE.equals(a.cacheTools));
        }
    }

    @Override
    public LanguageModelParameters collect(CommonModelParameters common) {
        AnthropicParameters p = new AnthropicParameters();
        copyCommon(common, p);
        p.topK = (Integer) topK.getValue();
        p.version = version.getText();
        p.beta = beta.getText();
        p.thinkingType = thinkingType.getText();
        p.thinkingBudgetTokens = (Integer) thinkingBudget.getValue();
        p.cacheSystemMessages = cacheSystem.isSelected();
        p.cacheTools = cacheTools.isSelected();
        return p;
    }
}
