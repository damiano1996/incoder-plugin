package com.github.damiano1996.jetbrains.incoder.language.model.server.ollama;

import static com.github.damiano1996.jetbrains.incoder.language.model.server.ollama.OllamaLanguageModelServer.OLLAMA;

import com.github.damiano1996.jetbrains.incoder.language.model.server.BaseProviderUIStrategy;
import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;

public class OllamaUIStrategy extends BaseProviderUIStrategy {
    private JSpinner topK;
    private JSpinner repeatPenalty;
    private JSpinner seed;
    private JSpinner numPredict;
    private JSpinner numCtx;

    public OllamaUIStrategy() {
        super(OLLAMA);
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
                                new JBLabel("RepeatPenalty:"),
                                (repeatPenalty =
                                        new JSpinner(new SpinnerNumberModel(1.1, 0.0, 10.0, 0.1))),
                                0,
                                false)
                        .addLabeledComponent(
                                new JBLabel("Seed:"),
                                (seed =
                                        new JSpinner(
                                                new SpinnerNumberModel(
                                                        0, 0, Integer.MAX_VALUE, 1))),
                                0,
                                false)
                        .addLabeledComponent(
                                new JBLabel("NumPredict:"),
                                (numPredict =
                                        new JSpinner(new SpinnerNumberModel(2048, 1, 128000, 1))),
                                0,
                                false)
                        .addLabeledComponent(
                                new JBLabel("NumCtx:"),
                                (numCtx =
                                        new JSpinner(
                                                new SpinnerNumberModel(8192, 256, 262144, 256))),
                                0,
                                false)
                        .getPanel();
        return panel;
    }

    @Override
    public void applyDefaults(LanguageModelParameters defaults) {
        if (defaults instanceof OllamaParameters ol) {
            topK.setValue(nz(ol.topK, 40));
            repeatPenalty.setValue(nz(ol.repeatPenalty, 1.1));
            seed.setValue(nz(ol.seed, 0));
            numPredict.setValue(nz(ol.numPredict, 2048));
            numCtx.setValue(nz(ol.numCtx, 8192));
        }
    }

    @Override
    public LanguageModelParameters collect(LanguageModelParameters common) {
        OllamaParameters p = new OllamaParameters();
        copyCommon(common, p);
        p.topK = (Integer) topK.getValue();
        p.repeatPenalty = ((Number) repeatPenalty.getValue()).doubleValue();
        p.seed = (Integer) seed.getValue();
        p.numPredict = (Integer) numPredict.getValue();
        p.numCtx = (Integer) numCtx.getValue();
        return p;
    }
}
