package com.yglong.plugin.intellij.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author longyg
 */
public interface DocBrowserToolWindowService {

    static DocBrowserToolWindowService getInstance(@NotNull Project project) {
        return project.getService(DocBrowserToolWindowService.class);
    }

    default void init(@NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ApplicationManager.getApplication().getService(ContentFactory.class);
        Content content = contentFactory.createContent(getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    void setup(@NotNull ToolWindow toolWindow);

    /**
     * 获取ToolWindow的显示内容
     *
     * @return
     */
    JComponent getContent();

    void updateContent(VirtualFile virtualFile);

    void openDocxFile();

    void saveChangesToDocxFile();

    void selectFileInTree(VirtualFile docFile, VirtualFile subFile);

    void selectFileInTree(VirtualFile file);
}
