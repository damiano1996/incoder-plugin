package com.github.damiano1996.jetbrains.incoder.language.model.server.openai;

import static com.github.damiano1996.jetbrains.incoder.language.model.server.openai.OpenAiLanguageModelServer.OPEN_AI;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.github.damiano1996.jetbrains.incoder.language.model.server.settings.ui.BaseProviderUIStrategy;
import com.github.damiano1996.jetbrains.incoder.language.model.server.settings.ui.CommonModelParameters;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;

public class OpenAiUIStrategy extends BaseProviderUIStrategy {
    private JSpinner maxCompletion;
    private JSpinner presencePenalty;
    private JSpinner frequencyPenalty;
    private JCheckBox strictJson;
    private JCheckBox strictTools;
    private JTextField orgId;
    private JTextField projectId;

    public OpenAiUIStrategy() {
        super(OPEN_AI);
    }

    @Override
    public JPanel buildPanel() {
        if (panel != null) return panel;
        panel =
                FormBuilder.createFormBuilder()
                        .addLabeledComponent(
                                new JBLabel("MaxCompletionTokens:"),
                                (maxCompletion =
                                        new JSpinner(new SpinnerNumberModel(1024, 1, 128000, 1))),
                                0,
                                false)
                        .addLabeledComponent(
                                new JBLabel("PresencePenalty:"),
                                (presencePenalty =
                                        new JSpinner(new SpinnerNumberModel(0.0, -2.0, 2.0, 0.1))),
                                0,
                                false)
                        .addLabeledComponent(
                                new JBLabel("FrequencyPenalty:"),
                                (frequencyPenalty =
                                        new JSpinner(new SpinnerNumberModel(0.0, -2.0, 2.0, 0.1))),
                                0,
                                false)
                        .addComponent((strictJson = new JCheckBox("Strict JSON schema")))
                        .addComponent((strictTools = new JCheckBox("Strict tools")))
                        .addLabeledComponent(
                                new JBLabel("OrganizationId:"),
                                (orgId = new JTextField()),
                                0,
                                false)
                        .addLabeledComponent(
                                new JBLabel("ProjectId:"), (projectId = new JTextField()), 0, false)
                        .getPanel();
        return panel;
    }

    @Override
    public void applyDefaults(LanguageModelParameters defaults) {
        if (defaults instanceof OpenAiParameters o) {
            maxCompletion.setValue(nz(o.maxCompletionTokens, 1024));
            presencePenalty.setValue(nz(o.presencePenalty, 0.0));
            frequencyPenalty.setValue(nz(o.frequencyPenalty, 0.0));
            strictJson.setSelected(Boolean.TRUE.equals(o.strictJsonSchema));
            strictTools.setSelected(Boolean.TRUE.equals(o.strictTools));
            orgId.setText(nz(o.organizationId, ""));
            projectId.setText(nz(o.projectId, ""));
        }
    }

    @Override
    public LanguageModelParameters collect(CommonModelParameters common) {
        OpenAiParameters p = new OpenAiParameters();
        copyCommon(common, p);
        p.maxCompletionTokens = (Integer) maxCompletion.getValue();
        p.presencePenalty = ((Number) presencePenalty.getValue()).doubleValue();
        p.frequencyPenalty = ((Number) frequencyPenalty.getValue()).doubleValue();
        p.strictJsonSchema = strictJson.isSelected();
        p.strictTools = strictTools.isSelected();
        p.organizationId = orgId.getText();
        p.projectId = projectId.getText();
        return p;
    }
}
