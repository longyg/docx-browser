package com.yglong.plugin.intellij.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.yglong.plugin.intellij.service.DocBrowserToolWindowService;
import org.jetbrains.annotations.NotNull;

/**
 * @author longyg
 */
public class DocBrowserToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        DocBrowserToolWindowService.getInstance(project).init(toolWindow);
    }
}
