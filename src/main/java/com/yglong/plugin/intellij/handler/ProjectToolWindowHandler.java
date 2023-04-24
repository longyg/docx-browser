package com.yglong.plugin.intellij.handler;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author longyg
 */
public class ProjectToolWindowHandler {

    private final Map<DocBrowserProjectHandler, ProjectContentManagerListener> registeredCmListeners;

    public ProjectToolWindowHandler() {
        registeredCmListeners = new HashMap<>();
    }

    protected String getToolWindowId() {
        return ToolWindowId.PROJECT_VIEW;
    }

    public void register(DocBrowserProjectHandler projectHandler) {
        if (registeredCmListeners.containsKey(projectHandler)) return;

        ToolWindow toolWindow = ToolWindowManager.getInstance(projectHandler.getProject())
                .getToolWindow(getToolWindowId());
        if (null == toolWindow) return;

        ContentManager cm = toolWindow.getContentManager();
        assert cm != null : "ContentManager required";
        ProjectContentManagerListener cmListener = new ProjectContentManagerListener(projectHandler);
        cmListener.registerCurrentTree();
        cm.addContentManagerListener(cmListener);
        registeredCmListeners.put(projectHandler, cmListener);
    }

    public void unregister(DocBrowserProjectHandler projectHandler) {
        if (!registeredCmListeners.containsKey(projectHandler)) return;

        ToolWindow toolWindow = ToolWindowManager.getInstance(projectHandler.getProject())
                .getToolWindow(getToolWindowId());

        if (null == toolWindow) return;

        ContentManager contentManager = toolWindow.getContentManager();
        ProjectContentManagerListener cmListener = registeredCmListeners.get(projectHandler);
        contentManager.removeContentManagerListener(cmListener);
        cmListener.unregisterCurrentTree();
        registeredCmListeners.remove(projectHandler);
    }

    public static class ProjectContentManagerListener implements ContentManagerListener {

        protected final DocBrowserProjectHandler projectHandler;

        public ProjectContentManagerListener(DocBrowserProjectHandler projectHandler) {
            this.projectHandler = projectHandler;
        }

        @Override
        public void selectionChanged(@NotNull ContentManagerEvent event) {
            if (event.getOperation() == ContentManagerEvent.ContentOperation.remove) {
//                PreviewUtil.closeAllPreviews(docBrowserProjectHandler.getProject());
                unregisterCurrentTree();
            }
            if (event.getOperation() == ContentManagerEvent.ContentOperation.add) {
//                PreviewUtil.invokeSafe(docBrowserProjectHandler.getProject(), () -> {
//                    registerCurrentTree();
//                });
            }
        }

        protected AbstractProjectViewPane getCurrentViewPane() {
            Project project = projectHandler.getProject();
            ProjectView projectView = ProjectView.getInstance(project);
            return projectView.getCurrentProjectViewPane();
        }

        public void unregisterCurrentTree() {
            AbstractProjectViewPane viewPane = getCurrentViewPane();
            if (viewPane != null) {
                projectHandler.unregisterTreeHandlers(viewPane.getTree());
            }
        }

        public void registerCurrentTree() {
            AbstractProjectViewPane viewPane = getCurrentViewPane();
            if (viewPane != null) {
                projectHandler.registerTreeHandlers(viewPane.getTree());
            }
        }
    }
}
