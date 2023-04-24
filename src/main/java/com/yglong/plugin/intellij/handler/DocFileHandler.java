package com.yglong.plugin.intellij.handler;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.io.ZipUtil;
import com.yglong.plugin.intellij.utils.DocBrowserUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import static com.yglong.plugin.intellij.constants.Constants.DOCX;

/**
 * Docx file handler, provide implementations for a certain docx file's operations:
 * <p>
 * 1) create temporary things for docx file when a docx file is selected from project view.
 * <p>
 * 2) save temporary changes to original docx file.
 * <p>
 * 3) dispose temporary things before IDEA exiting.
 * <p>
 * 4) rebuild temporary things when original docx file changed.
 * <p>
 * 5) open temporary docx file in word application.
 *
 * @author longyg
 */
public class DocFileHandler {
    private final VirtualFile docFile;
    private File tempDir;
    private File tempFile;
    private final Set<File> tempFileSets = new HashSet<>();
    private boolean initialized;

    public DocFileHandler(VirtualFile docFile) {
        this.docFile = docFile;
        init();
    }

    private void init() {
        if (null == docFile) return;
        if (initialized) return;

        tempDir = createTempDocxDirectory();
        tempFile = createTempDocxFile();
        if (null == tempDir || null == tempFile) return;

        tempFileSets.add(tempFile);

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            IOUtils.copy(docFile.getInputStream(), fos);
            ZipUtil.extract(tempFile.toPath(), tempDir.toPath(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initialized = true;
    }

    public VirtualFile getDocFile() {
        return docFile;
    }

    public File getTempFile(String relativePath) {
        return new File(tempDir.getPath() + File.separator + relativePath);
    }

    public File getTempFile() {
        return tempFile;
    }

    public void openTempFile() {
        saveToTempFile();
        try {
            Desktop.getDesktop().open(getTempFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveToTempFile() {
        if (null == tempDir) return;
        File newTempFile = createTempDocxFile();
        if (null == newTempFile) return;
        DocBrowserUtil.zip(newTempFile, tempDir, "");
        if (delete(tempFile)) {
            tempFileSets.remove(tempFile);
        }
        tempFile = newTempFile;
        tempFileSets.add(newTempFile);
    }

    private File createTempDocxDirectory() {
        try {
            File dir = Files.createTempDirectory(docFile.getName()).toFile();
            FileUtils.forceDeleteOnExit(dir);
            return dir;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private File createTempDocxFile() {
        try {
            File file = File.createTempFile(docFile.getName(), DOCX);
            FileUtils.forceDeleteOnExit(file);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveToDocFile() {
        if (null == tempFile || null == docFile) return;
        saveToTempFile();
        try (FileInputStream fis = new FileInputStream(tempFile);
             FileOutputStream fos = new FileOutputStream(docFile.getPath())) {
            IOUtils.copy(fis, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        clearTempFileSets();
        delete(tempDir);
    }

    private void clearTempFileSets() {
        if (tempFileSets.isEmpty()) return;
        for (File file : tempFileSets) {
            delete(file);
        }
        tempFileSets.clear();
    }

    private boolean delete(File file) {
        if (null == file) return false;
        if (!file.exists()) return true;

        if (file.isDirectory()) {
            return deleteDir(file);
        }

        return deleteFile(file);
    }

    private boolean deleteDir(File dir) {
        if (null == dir) return false;
        try {
            FileUtils.deleteDirectory(dir);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to delete directory: " + dir.getPath());
            e.printStackTrace();
        }
        return false;
    }

    private boolean deleteFile(File file) {
        if (null == file) return false;
        if (!file.exists()) return true;
        try {
            FileUtils.forceDelete(file);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to delete file: " + file.getPath());
            e.printStackTrace();
        }
        return false;
    }

    public void rebuild() {
        if (null == docFile) return;
        initialized = false;
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            IOUtils.copy(docFile.getInputStream(), fos);
            cleanDirectory(tempDir);
            ZipUtil.extract(tempFile.toPath(), tempDir.toPath(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initialized = true;
    }

    private void cleanDirectory(File dir) {
        if (null == dir || !dir.isDirectory()) return;
        File[] files = dir.listFiles();
        if (null == files) return;
        for (File file : files) {
            delete(file);
        }
    }
}
