package com.yglong.plugin.intellij.file;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.impl.ArchiveHandler;
import com.intellij.openapi.vfs.newvfs.ArchiveFileSystem;
import com.intellij.openapi.vfs.newvfs.VfsImplUtil;
import com.intellij.util.io.URLUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.yglong.plugin.intellij.constants.Constants.DOCX;

/**
 * @author longyg
 */
public class DocxFileSystem extends ArchiveFileSystem {
    private String fsSeparator = URLUtil.JAR_SEPARATOR;

    private static final DocxFileSystem instance = new DocxFileSystem();

    public static DocxFileSystem getInstance() {
        return instance;
    }

    @Override
    protected @NotNull String extractLocalPath(@NotNull String rootPath) {
        return StringUtil.trimEnd(rootPath, fsSeparator);
    }

    @Override
    protected @NotNull String composeRootPath(@NotNull String localPath) {
        return localPath + fsSeparator;
    }

    @Override
    protected @NotNull ArchiveHandler getHandler(@NotNull VirtualFile entryFile) {
        return VfsImplUtil.getHandler(this, entryFile, DocxHandler::new);
    }

    @Override
    protected @NotNull String extractRootPath(@NotNull String normalizedPath) {
        int sIndex = normalizedPath.indexOf(fsSeparator);
        assert sIndex >= 0 : "Path must have separator !/";
        return normalizedPath.substring(0, sIndex + fsSeparator.length());
    }

    @Override
    public @Nullable VirtualFile findFileByPathIfCached(@NonNls @NotNull String path) {
        return VfsImplUtil.findFileByPathIfCached(this, path);
    }

    @Override
    public @NonNls @NotNull String getProtocol() {
        return URLUtil.FILE_PROTOCOL;
    }

    @Override
    public @Nullable VirtualFile findFileByPath(@NotNull @NonNls String path) {
        return VfsImplUtil.findFileByPath(this, path);
    }

    @Override
    protected boolean isCorrectFileType(@NotNull VirtualFile local) {
        return local.getName().endsWith(DOCX);
    }

    @Override
    public void refresh(boolean asynchronous) {
        VfsImplUtil.refresh(this, asynchronous);
    }

    @Override
    public @Nullable VirtualFile refreshAndFindFileByPath(@NotNull String path) {
        return VfsImplUtil.refreshAndFindFileByPath(this, path);
    }
}
