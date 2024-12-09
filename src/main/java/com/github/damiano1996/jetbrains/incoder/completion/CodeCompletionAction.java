package com.github.damiano1996.jetbrains.incoder.completion;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class CodeCompletionAction extends AnAction implements AnActionListener {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project == null) return;

        CodeCompletionService.getInstance(project).actionPerformed(anActionEvent);
    }

    @Override
    public void beforeActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) return;

        CodeCompletionService.getInstance(project).beforeActionPerformed(action, event);
    }
}
