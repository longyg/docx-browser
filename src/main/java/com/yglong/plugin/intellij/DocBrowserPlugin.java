package com.yglong.plugin.intellij;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.util.PsiUtilCore;
import com.yglong.plugin.intellij.handler.DocFileHandler;
import com.yglong.plugin.intellij.service.DocBrowserToolWindowService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represent our plugin, provide some plugin level common operations.
 *
 * @author longyg
 */
public class DocBrowserPlugin {

    /**
     * docx file to file handler mapping
     */
    private final Map<VirtualFile, DocFileHandler> fileHandlers = new ConcurrentHashMap<>();

    /**
     * docx file to sub files documents mapping
     */
    private final Map<VirtualFile, Set<Document>> fileDocuments = new ConcurrentHashMap<>();

    /**
     * Sub file to docx file mapping
     */
    private final Map<VirtualFile, VirtualFile> registeredSubFiles = new ConcurrentHashMap<>();

    private static final DocBrowserPlugin instance = new DocBrowserPlugin();

    public static DocBrowserPlugin getInstance() {
        return instance;
    }

    /**
     * Register a docx file to our plugin, create file handler for it.
     */
    public void register(VirtualFile virtualFile) {
        if (!isRegisteredDocFile(virtualFile)) {
            fileHandlers.put(virtualFile, new DocFileHandler(virtualFile));
        }
    }

    /**
     * Register a sub file's document to our plugin when a sub file of docx file is opened in editor.
     */
    public void registerFileSubDocument(VirtualFile virtualFile, Document document) {
        Set<Document> set = fileDocuments.getOrDefault(virtualFile, new HashSet<>());
        set.add(document);
        fileDocuments.put(virtualFile, set);
    }

    /**
     * Register a sub file to our plugin when a sub file of docx file is opened in editor.
     */
    public void registerSubFile(VirtualFile subFile, VirtualFile docFile) {
        registeredSubFiles.putIfAbsent(subFile, docFile);
    }

    /**
     * Flush and save all sub file's document of a docx file.
     */
    public void flush(VirtualFile file) {
        if (!fileDocuments.containsKey(file)) return;
        for (Document document : fileDocuments.get(file)) {
            FileDocumentManager.getInstance().saveDocument(document);
        }
    }

    /**
     * Refresh docx file related all things. Basically rebuild it's temporary things due to the docx file was changed.
     */
    public void refresh(VirtualFile file) {
        if (!isRegisteredDocFile(file)) return;

        DocFileHandler fileHandler = fileHandlers.get(file);
        fileHandler.rebuild();
    }

    /**
     * When a sub file of a docx file in editor is focused, this method is called.
     * We do related things for the focus event:
     * <p>
     * 1) Open related docx file in tool window.
     * <p>
     * 2) Focus related docx file in project view.
     */
    public void focus(Project project, VirtualFile editorFile) {
        if (!isRegisteredEditableFile(editorFile)) return;
        VirtualFile docFile = registeredSubFiles.get(editorFile);
        // select in tool window
        DocBrowserToolWindowService toolWindowService = DocBrowserToolWindowService.getInstance(project);
        toolWindowService.selectFileInTree(docFile, editorFile);

        // select in project view
        VirtualFile target = docFile.getCanonicalFile();
        PsiFileSystemItem psiFile = PsiUtilCore.findFileSystemItem(project, target);
        if (psiFile != null) {
            ProjectView.getInstance(project).select(psiFile, target, false);
        }
    }

    /**
     * Dispose everything, basically the temporary things. It is called before IDEA exiting.
     */
    public void dispose() {
        fileHandlers.values().forEach(DocFileHandler::dispose);
    }


    private boolean isRegisteredDocFile(VirtualFile file) {
        return fileHandlers.containsKey(file);
    }

    private boolean isRegisteredEditableFile(VirtualFile file) {
        return registeredSubFiles.containsKey(file);
    }

    public Map<VirtualFile, DocFileHandler> getFileHandlers() {
        return fileHandlers;
    }


    public DocFileHandler getFileHandler(VirtualFile virtualFile) {
        return fileHandlers.get(virtualFile);
    }
}
