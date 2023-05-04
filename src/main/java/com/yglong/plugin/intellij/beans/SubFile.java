package com.yglong.plugin.intellij.beans;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.vfs.VirtualFile;
import com.yglong.plugin.intellij.handler.DocFileHandler;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;

/**
 * Represent a sub file inside a docx file, e.g., document.xml
 *
 * @author longyg
 */
public class SubFile implements NodeData {
    private final String name;

    private SubFileType type;
    private String basePath;
    private String filePath;

    private InputStream data;

    /**
     * The file handler associated to the docx file which contains this sub file
     */
    private DocFileHandler fileHandler;

    public SubFile(DocFileHandler fileHandler, SubFileType type, String name, String basePath, String filePath, InputStream data) {
        this.fileHandler = fileHandler;
        this.type = type;
        this.name = name;
        this.basePath = basePath;
        this.filePath = filePath;
        this.data = data;
    }

    public VirtualFile getDocFile() {
        return fileHandler.getDocFile();
    }

    public File getTempFile() {
        return fileHandler.getTempFile(filePath);
    }

    public String getName() {
        return name;
    }

    public SubFileType getType() {
        return type;
    }

    public void setType(SubFileType type) {
        this.type = type;
    }

    public DocFileHandler getFileHandler() {
        return fileHandler;
    }

    public void setFileHandler(DocFileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public InputStream getData() {
        return data;
    }

    public void setData(InputStream data) {
        this.data = data;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Icon getIcon() {
        switch (getFileType()) {
            case REL:
                return AllIcons.FileTypes.Manifest;
            case XML:
                return AllIcons.FileTypes.Xml;
            case IMAGE:
                return AllIcons.FileTypes.Diagram;
            default:
                return AllIcons.FileTypes.Any_type;
        }
    }

    private SubFileType getFileType() {
        if (name.endsWith(".xml")) {
            return SubFileType.XML;
        } else if (name.endsWith(".rels")) {
            return SubFileType.REL;
        } else if (name.endsWith(".png") || name.endsWith(".jpg") ||
                name.endsWith(".jpeg") || name.endsWith(".svg") || name.endsWith(".gif")) {
            return SubFileType.IMAGE;
        } else {
            return SubFileType.UNKNOWN;
        }
    }
}
