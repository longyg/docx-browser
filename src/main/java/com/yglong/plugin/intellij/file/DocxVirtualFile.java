package com.yglong.plugin.intellij.file;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author longyg
 */
public class DocxVirtualFile extends VirtualFile {
    private String name;
    private String path;
    private InputStream data;

    public DocxVirtualFile(String name, String path, InputStream data) {
        this.name = name;
        this.path = path;
        this.data = data;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull VirtualFileSystem getFileSystem() {
        return DocxFileSystem.getInstance();
    }

    @Override
    public @NonNls @NotNull String getPath() {
        return null;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public VirtualFile getParent() {
        return null;
    }

    @Override
    public VirtualFile[] getChildren() {
        return new VirtualFile[0];
    }

    @Override
    public @NotNull OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) throws IOException {
        return null;
    }

    @Override
    public byte @NotNull [] contentsToByteArray() throws IOException {
        return new byte[0];
    }

    @Override
    public long getTimeStamp() {
        return 0;
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public void refresh(boolean asynchronous, boolean recursive, @Nullable Runnable postRunnable) {

    }

    @Override
    public @NotNull InputStream getInputStream() throws IOException {
        return null;
    }
}
