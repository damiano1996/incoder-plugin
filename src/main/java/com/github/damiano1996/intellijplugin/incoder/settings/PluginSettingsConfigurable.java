package com.github.damiano1996.intellijplugin.incoder.settings;

import com.github.damiano1996.intellijplugin.incoder.InCoderActivity;
import com.github.damiano1996.intellijplugin.incoder.language.model.LanguageModelException;
import com.github.damiano1996.intellijplugin.incoder.language.model.server.settings.ServerSettingsConfigurable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.ui.FormBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.*;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PluginSettingsConfigurable implements Configurable {

    @Contract(pure = true)
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public @NotNull String getDisplayName() {
        return "InCoder Settings";
    }


    @Contract(pure = true)
    @Override
    public @Nullable JComponent createComponent() {
        return null;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() {
    }

}
