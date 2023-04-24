package com.yglong.plugin.intellij.listener;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.yglong.plugin.intellij.DocBrowserPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author longyg
 */
public class DocBrowserFileListener implements BulkFileListener {

    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        for (VFileEvent event : events) {
            VirtualFile file = event.getFile();
            DocBrowserPlugin.getInstance().refresh(file);
        }
    }
}
