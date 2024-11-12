package com.github.damiano1996.intellijplugin.incoder.generation;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public final class PromptToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        PromptToolWindowContent toolWindowContent =
                new PromptToolWindowContent(project, toolWindow);
        Content content =
                ContentFactory.getInstance()
                        .createContent(toolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static class PromptToolWindowContent {

        private final Project project;

        @Getter private final JPanel contentPanel;
        private final JBTextField promptTextField = new JBTextField();
        private final JButton submitButton = new JButton("Submit");

        public PromptToolWindowContent(Project project, ToolWindow toolWindow) {
            this.project = project;

            promptTextField.addActionListener(this::handleAction);
            submitButton.addActionListener(this::handleAction);

            contentPanel =
                    FormBuilder.createFormBuilder()
                            .setVerticalGap(5)
                            .setFormLeftIndent(20)
                            .addLabeledComponent(
                                    new JBLabel("Enter prompt:"),
                                    new JPanel(new BorderLayout()) {
                                        {
                                            setBorder(JBUI.Borders.emptyRight(10));
                                            add(promptTextField);
                                        }
                                    },
                                    1,
                                    true)
                            .addComponent(
                                    new JPanel(new BorderLayout()) {
                                        {
                                            setBorder(JBUI.Borders.emptyRight(10));
                                            add(new JButton("Submit"), BorderLayout.EAST);
                                        }
                                    })
                            .setFormLeftIndent(0)
                            .addComponentFillVertically(new JPanel(), 0)
                            .getPanel();
        }

        private void handleAction(ActionEvent e) {
            String prompt = promptTextField.getText();
            handlePrompt(prompt);
        }

        private void handlePrompt(@NotNull String prompt) {
            if (prompt.isEmpty()) {
                log.debug("Prompt is empty.");
            } else {
                log.debug("Prompt: {}", prompt);
                promptTextField.setText("");
                CodeGenerationService.getInstance(project).updateCode(prompt);
            }
        }
    }
}
