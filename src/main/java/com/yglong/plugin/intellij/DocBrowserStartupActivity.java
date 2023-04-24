package com.yglong.plugin.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.util.messages.MessageBusConnection;
import com.yglong.plugin.intellij.handler.DocBrowserProjectHandler;
import com.yglong.plugin.intellij.handler.ProjectToolWindowHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author longyg
 */
public class DocBrowserStartupActivity implements StartupActivity, DumbAware {
    protected ConcurrentHashMap<Project, DocBrowserProjectHandler> myProjectHandlerMap = new ConcurrentHashMap<>();

    private ProjectToolWindowHandler projectToolWindowHandler;

    protected void initialize(Project project, MessageBusConnection connection) {
        projectToolWindowHandler = new ProjectToolWindowHandler();
        ApplicationManager.getApplication().invokeLater(() -> {
            if (isInitialized(project) || project.isDisposed()) return;

            DocBrowserProjectHandler projectHandler = new DocBrowserProjectHandler(project, connection);
            myProjectHandlerMap.put(project, projectHandler);
            registerProjectToolWindow(project);
        });
    }

    private void registerProjectToolWindow(Project project) {
        if (!isInitialized(project)) return;

        DocBrowserProjectHandler projectHandler = myProjectHandlerMap.get(project);
        projectToolWindowHandler.register(projectHandler);
    }

    private void unregisterProjectToolWindow(Project project) {
        if (!isInitialized(project)) {
            return;
        }
        DocBrowserProjectHandler projectHandler = myProjectHandlerMap.get(project);
        projectToolWindowHandler.unregister(projectHandler);
    }

    @Override
    public void runActivity(@NotNull Project project) {
        if (isInitialized(project)) return;

        MessageBusConnection connection = project.getMessageBus().connect();
        connection.subscribe(ToolWindowManagerListener.TOPIC, new ToolWindowManagerListener() {

            @Override
            public void toolWindowsRegistered(@NotNull List<String> ids, @NotNull ToolWindowManager toolWindowManager) {
                if (!isInitialized(project)) {
                    initialize(project, connection);
                }
                if (ids.contains(ToolWindowId.PROJECT_VIEW)) {
                    registerProjectToolWindow(project);
                }
            }

            @Override
            public void toolWindowUnregistered(@NotNull String id, @NotNull ToolWindow toolWindow) {
                if (ToolWindowId.PROJECT_VIEW.equals(id)) {
                    dispose(project, connection);
                }
            }

            @Override
            public void stateChanged(@NotNull ToolWindowManager toolWindowManager) {
                ToolWindow window = toolWindowManager.getToolWindow(ToolWindowId.PROJECT_VIEW);
                if (window != null && !isInitialized(project)) {
                    initialize(project, connection);
                }
            }
        });
        connection.subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {
            @Override
            public void projectClosingBeforeSave(@NotNull Project curProject) {
                if (project.equals(curProject)) {
                    dispose(project, connection);
                }
            }
        });

        // try to register at startup
        initialize(project, connection);
    }

    protected void dispose(Project project, MessageBusConnection connection) {
        DocBrowserProjectHandler projectHandler = myProjectHandlerMap.get(project);
        if (projectHandler != null) {
            unregisterProjectToolWindow(project);
            myProjectHandlerMap.remove(project);
            projectHandler.dispose();
            connection.disconnect();
            connection.dispose();
        }
    }

    protected boolean isInitialized(Project project) {
        return myProjectHandlerMap.containsKey(project);
    }
}
