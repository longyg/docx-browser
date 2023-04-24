package com.yglong.plugin.intellij.handler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.util.messages.MessageBusConnection;
import com.yglong.plugin.intellij.listener.ProjectTreeSelectionListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import java.util.ArrayList;
import java.util.List;

public class DocBrowserProjectHandler {

    public static final Key<String> PREVIEW_VIRTUAL_FILE_KEY = Key.create(DocBrowserProjectHandler.class.getName());

    private Project myProject;

    private final List<JTree> registeredTrees = new ArrayList<>();

    private final TreeSelectionListener myTreeSelectionListener;

//    private final MouseListener myTreeMouseListener = new MouseAdapter() {
//        @Override
//        public void mouseReleased(MouseEvent mouseEvent) {
//            switch (mouseEvent.getClickCount()) {
//                case 1:
//                    // one-click behavior is handled by myTreeSelectionListener
//                    break;
//                case 2:
//                    if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
//                        PreviewUtil.consumeSelectedFile(mouseEvent.getComponent(), selectedFile -> PreviewUtil.disposePreview(myProject, PreviewUtil.getGotoFile(myProject, selectedFile)));
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    };


    public DocBrowserProjectHandler(@NotNull Project project, @NotNull MessageBusConnection messageBusConnection) {
        assert myProject == null : "already initialized";
        myProject = project;
        myTreeSelectionListener = new ProjectTreeSelectionListener(project);
    }

    public void dispose() {
        assert myProject != null : "not initialized yet";

//        PreviewUtil.closeAllPreviews(myProject);
//
//        PreviewSettings previewSettings = PreviewSettings.getInstance();
//        previewSettings.removePropertyChangeListener(mySettingsPropertyChangeListener);

        unregisterAllTreeHandlers();

        myProject = null;
    }

    public void registerTreeHandlers(@NotNull final JTree tree) {
        if (areTreeHandlersRegistered(tree)) {
            return;
        }
        tree.addTreeSelectionListener(myTreeSelectionListener);
//        tree.addMouseListener(myTreeMouseListener);
        registeredTrees.add(tree);
    }

    public void unregisterAllTreeHandlers() {
        for (JTree tree : new ArrayList<>(registeredTrees)) {
            unregisterTreeHandlers(tree);
        }
    }

    public void unregisterTreeHandlers(@NotNull final JTree tree) {
        if (!areTreeHandlersRegistered(tree)) {
            return;
        }

        registeredTrees.remove(tree);
        tree.removeTreeSelectionListener(myTreeSelectionListener);
//        tree.removeKeyListener(myTreeKeyListener);
//        tree.removeMouseListener(myTreeMouseListener);
    }

    public boolean areTreeHandlersRegistered(@NotNull final JTree tree) {
        return registeredTrees.contains(tree);
    }

//    protected void focusComponentIfSelectedFileIsNotOpen(final Component component) {
//        PreviewUtil.consumeSelectedFile(component, selectedFile -> {
//            if (selectedFile == null) {
//                return;
//            }
//            final FileEditorManager fileEditorManager = FileEditorManager.getInstance(myProject);
//            final VirtualFile gotoFile = PreviewUtil.getGotoFile(myProject, selectedFile);
//            if (PreviewUtil.isProjectTreeFocused(myProject) ||
//                    (gotoFile != null && !fileEditorManager.isFileOpen(gotoFile))) {
//                component.requestFocus();
//            }
//        });
//    }
//
//    public void openOrFocusSelectedFile(final Component component) {
//        if (PreviewUtil.isAutoScrollToSource(myProject)) {
//            return;
//        }
//        // - "Open declaration source in the same tab" is focus based (#29) - ensure that component has focus
//        // - "Autoscroll from Source" triggers this function as well when switching tabs (#44) - focus shouldn't change
//        focusComponentIfSelectedFileIsNotOpen(component);
//        PreviewUtil.invokeSafe(myProject, () -> {
//            switch (PreviewSettings.getInstance().getPreviewBehavior()) {
//                case PREVIEW_BY_DEFAULT:
//                    PreviewUtil.openPreviewOrEditor(myProject, component);
//                    break;
//                case EXPLICIT_PREVIEW:
//                    PreviewUtil.consumeSelectedFile(component, file -> {
//                        focusFileEditor(PreviewUtil.getGotoFile(myProject, file), false);
//                    });
//                    break;
//                default:
//                    throw new UnsupportedOperationException(String.format("case '%s' not handled", PreviewSettings.getInstance().getPreviewBehavior()));
//            }
//        });
//    }
//
//    public void focusFileEditor(VirtualFile file, boolean focusEditor) {
//        if (!isValid() || file == null) {
//            return;
//        }
//        final FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(myProject);
//        if (!fileEditorManager.isFileOpen(file)) {
//            return;
//        }
//        PreviewUtil.invokeSafe(myProject, () -> fileEditorManager.openFile(file, focusEditor));
//    }
//
//    public boolean isValid() {
//        return PreviewUtil.isValid(myProject);
//    }

    public Project getProject() {
        return myProject;
    }
}
