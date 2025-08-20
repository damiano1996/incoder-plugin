package com.github.damiano1996.jetbrains.incoder.language.model.server.settings.ui;

import java.awt.*;
import javax.swing.*;

public abstract class BaseProviderUIStrategy implements ProviderUIStrategy {
    protected final String providerName;
    protected JPanel panel;

    protected BaseProviderUIStrategy(String providerName) {
        this.providerName = providerName;
    }

    @Override
    public String name() {
        return providerName;
    }

    @Override
    public String cardName() {
        return providerName.toUpperCase();
    }

    @Override
    public JPanel buildPanel() {
        if (panel == null) panel = new JPanel(new BorderLayout());
        return panel;
    }

    public static <T> T nz(T v, T def) {
        return v != null ? v : def;
    }
}
