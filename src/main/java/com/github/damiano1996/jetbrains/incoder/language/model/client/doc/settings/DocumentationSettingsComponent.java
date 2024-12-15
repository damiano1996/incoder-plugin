package com.github.damiano1996.jetbrains.incoder.language.model.client.doc.settings;

import com.intellij.openapi.ui.DescriptionLabel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;
import lombok.Getter;

@Getter
public class DocumentationSettingsComponent {

    private final JPanel mainPanel;
    private final JBTextArea documentationInstructionsTextArea;

    public DocumentationSettingsComponent() {
        documentationInstructionsTextArea = new JBTextArea(5, 20);
        documentationInstructionsTextArea.setLineWrap(true);
        documentationInstructionsTextArea.setWrapStyleWord(true);

        mainPanel =
                FormBuilder.createFormBuilder()
                        .setFormLeftIndent(20)
                        .addLabeledComponent(
                                new JBLabel("System message instructions to write documentation:"),
                                ScrollPaneFactory.createScrollPane(
                                        documentationInstructionsTextArea),
                                1,
                                true)
                        .addComponent(
                                new DescriptionLabel(
                                        "System message template instructions to write"
                                            + " documentation.These instructions will be added to"
                                            + " the @SystemMessage."))
                        .setFormLeftIndent(0)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();
    }
}
