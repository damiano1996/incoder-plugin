package com.github.damiano1996.intellijplugin.incoder.language.model.client.inline.settings;

import com.github.damiano1996.intellijplugin.incoder.ui.components.DescriptionLabel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;
import lombok.Getter;

@Getter
public class InlineComponent {

    private final JPanel mainPanel;
    private final JBTextArea systemMessageInstructionsField;
    private final JBCheckBox enableCheckbox;

    public InlineComponent() {
        enableCheckbox = new JBCheckBox("Inline coding assistant");
        enableCheckbox.setSelected(true);

        systemMessageInstructionsField = new JBTextArea(5, 20);
        systemMessageInstructionsField.setLineWrap(true);
        systemMessageInstructionsField.setWrapStyleWord(true);

        mainPanel =
                FormBuilder.createFormBuilder()
                        .setFormLeftIndent(20)
                        .addComponent(enableCheckbox)
                        .addComponent(
                                new DescriptionLabel(
                                        "Enable the inline coding assistant functionality."))
                        .addVerticalGap(20)
                        .addLabeledComponent(
                                new JBLabel("System message instructions:"),
                                ScrollPaneFactory.createScrollPane(systemMessageInstructionsField),
                                1,
                                true)
                        .addComponent(
                                new DescriptionLabel(
                                        "System message template instructions. These instructions"
                                                + " will be added to the @SystemMessage with an"
                                                + " additional context."))
                        .setFormLeftIndent(0)
                        .addComponentFillVertically(new JPanel(), 0)
                        .getPanel();
    }
}
