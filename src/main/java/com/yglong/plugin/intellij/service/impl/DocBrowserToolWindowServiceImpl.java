package com.yglong.plugin.intellij.service.impl;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import com.yglong.plugin.intellij.DocBrowserPlugin;
import com.yglong.plugin.intellij.service.DocBrowserToolWindowService;
import com.yglong.plugin.intellij.toolwindow.DocBrowserToolWindow;
import com.yglong.plugin.intellij.utils.DocBrowserUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author longyg
 */
public class DocBrowserToolWindowServiceImpl implements DocBrowserToolWindowService {

    private final Project project;

    private DocBrowserToolWindow docBrowserToolWindow;

    private ContentManager contentManager;

    private DocBrowserPlugin myPlugin;

    public DocBrowserToolWindowServiceImpl(Project project) {
        this.project = project;
        this.myPlugin = DocBrowserPlugin.getInstance();
        docBrowserToolWindow = new DocBrowserToolWindow(project, myPlugin);
    }

    @Override
    public void setup(@NotNull ToolWindow toolWindow) {
        contentManager = toolWindow.getContentManager();

        getContentManager().addContentManagerListener(new ContentManagerListener() {
            @Override
            public void selectionChanged(@NotNull ContentManagerEvent event) {
                System.out.println(event.getSource());
                if (event.getOperation() == ContentManagerEvent.ContentOperation.add) {
                    viewSelectionChanged();
                }
            }
        });
        viewSelectionChanged();
    }

    private void viewSelectionChanged() {
        System.out.println("view selection changed");
    }

    @Override
    public JComponent getContent() {
        if (null == myPlugin) {
            myPlugin = DocBrowserPlugin.getInstance();
        }
        if (null == docBrowserToolWindow) {
            docBrowserToolWindow = new DocBrowserToolWindow(project, myPlugin);
        }
        return docBrowserToolWindow;
    }

    @Override
    public void updateContent(VirtualFile virtualFile) {
        if (null != docBrowserToolWindow) {
            DocBrowserUtil.invokeSafe(project, () -> docBrowserToolWindow.updateContent(virtualFile));
        }
    }

    @Override
    public void openDocxFile() {
        if (null != docBrowserToolWindow) {
            docBrowserToolWindow.openCurrentDocxFile();
        }
    }

    public ContentManager getContentManager() {
        if (contentManager == null) {
            contentManager = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.PROJECT_VIEW).getContentManager();
        }
        return contentManager;
    }

    @Override
    public void saveChangesToDocxFile() {
        if (null != docBrowserToolWindow) {
            docBrowserToolWindow.saveChangesToDocxFile();
        }
    }


    @Override
    public void selectFileInTree(VirtualFile docFile, VirtualFile subFile) {
        if (null != docBrowserToolWindow) {
            DocBrowserUtil.invokeSafe(project, () -> {
                docBrowserToolWindow.updateContent(docFile);
                selectFileInTree(subFile);
            });
        }
    }

    public void selectFileInTree(VirtualFile file) {
        if (null != docBrowserToolWindow) {
            DocBrowserUtil.invokeSafe(project, () -> docBrowserToolWindow.selectFileInTree(file));
        }
    }
}
