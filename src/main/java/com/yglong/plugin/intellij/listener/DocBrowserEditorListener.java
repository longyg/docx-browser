package com.yglong.plugin.intellij.listener;

import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.yglong.plugin.intellij.DocBrowserPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * @author longyg
 */
public class DocBrowserEditorListener implements FileEditorManagerListener {

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        VirtualFile virtualFile = event.getNewFile();
        if (null != virtualFile) {
            DocBrowserPlugin.getInstance().focus(event.getManager().getProject(), virtualFile);
        }
    }
}
