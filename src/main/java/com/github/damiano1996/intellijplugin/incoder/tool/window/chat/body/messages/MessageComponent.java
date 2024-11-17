package com.github.damiano1996.intellijplugin.incoder.tool.window.chat.body.messages;

import com.intellij.openapi.project.Project;

import javax.swing.*;

public interface MessageComponent extends StreamWriter {

    void setProject(Project project);

    JPanel getMainPanel();
}
