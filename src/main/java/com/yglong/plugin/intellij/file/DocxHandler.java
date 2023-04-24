package com.yglong.plugin.intellij.file;

import com.intellij.openapi.vfs.impl.ZipHandler;
import org.jetbrains.annotations.NotNull;

/**
 * @author longyg
 */
public class DocxHandler extends ZipHandler {

    public DocxHandler(@NotNull String path) {
        super(path);
    }
}
