package com.github.damiano1996.intellijplugin.incoder.llm.container.server.settings;

import com.github.damiano1996.intellijplugin.incoder.llm.container.server.orchestration.orchestrators.OrchestratorType;
import com.github.damiano1996.intellijplugin.incoder.llm.container.server.settings.renderers.HuggingFaceModelRenderer;
import com.github.damiano1996.intellijplugin.incoder.llm.server.settings.CustomServerSettingsComponent;
import com.github.damiano1996.intellijplugin.incoder.llm.server.settings.ServerSettings;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;
import lombok.Getter;

@Getter
public class ContainerSettingsComponent implements CustomServerSettingsComponent {

    private final JPanel mainPanel;

    private final ComboBox<OrchestratorType> orchestratorTypeComboBox =
            new ComboBox<>(OrchestratorType.values());

    private final JBTextField localHostPortNumberField = new JBTextField();

    private final JBCheckBox useCudaCheckBox = new JBCheckBox("Use CUDA when available");

    private final HuggingFaceModelComboBox huggingFaceModelComboBox =
            new HuggingFaceModelComboBox();
    private final TextFieldWithBrowseButton huggingFaceHomeField = new TextFieldWithBrowseButton();
    private final TextFieldWithBrowseButton huggingFaceHubCacheField =
            new TextFieldWithBrowseButton();

    public ContainerSettingsComponent() {
        FileChooserDescriptor descriptor =
                FileChooserDescriptorFactory.createSingleFolderDescriptor();
        huggingFaceHomeField.addBrowseFolderListener(
                "Select Hugging Face Home Directory", null, null, descriptor);
        huggingFaceHubCacheField.addBrowseFolderListener(
                "Select Hugging Face Hub Cache Directory", null, null, descriptor);

        mainPanel =
                FormBuilder.createFormBuilder()
                        .addComponent(new TitledSeparator("Container Settings"))
                        .setVerticalGap(5)
                        .setFormLeftIndent(20)
                        .addLabeledComponent(
                                new JBLabel("Local host port:"), localHostPortNumberField, 1, false)
                        .addVerticalGap(5)
                        // Orchestrator settings
                        .addComponent(new TitledSeparator("Orchestrator"))
                        .setFormLeftIndent(40)
                        .addLabeledComponent(
                                new JBLabel("Type:"), orchestratorTypeComboBox, 1, false)
                        .setFormLeftIndent(20)
                        .addVerticalGap(5)
                        // Hugging Face Settings
                        .addComponent(new TitledSeparator("Hugging Face"))
                        .setFormLeftIndent(40)
                        .addLabeledComponent(
                                new JBLabel("Model:"), huggingFaceModelComboBox, 1, false)
                        .addLabeledComponent(new JBLabel("Home:"), huggingFaceHomeField, 1, false)
                        .addLabeledComponent(
                                new JBLabel("Hub cache:"), huggingFaceHubCacheField, 1, false)
                        .setFormLeftIndent(20)
                        .addVerticalGap(5)
                        // CUDA settings
                        .addComponent(new TitledSeparator("CUDA"))
                        .setFormLeftIndent(40)
                        .addComponent(useCudaCheckBox, 1)
                        .setFormLeftIndent(0)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();
    }

    @Override
    public ServerSettings.State.ServerType getServerType() {
        return ServerSettings.State.ServerType.LOCAL_CONTAINER;
    }

    @Override
    public Configurable getConfigurable() {
        return new ContainerSettingsConfigurable(this);
    }

    public static class HuggingFaceModelComboBox
            extends ComboBox<ContainerSettings.State.HuggingFace.Model> {
        public HuggingFaceModelComboBox() {
            super(ContainerSettings.State.HuggingFace.Model.values());
            setRenderer(new HuggingFaceModelRenderer());
        }
    }
}
