package com.yglong.plugin.intellij.toolwindow;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBSplitter;
import com.yglong.plugin.intellij.DocBrowserPlugin;
import com.yglong.plugin.intellij.handler.DocFileHandler;
import com.yglong.plugin.intellij.utils.DocBrowserUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author longyg
 */
public class DocBrowserToolWindow extends JPanel {
    private final Project project;

    private final DocBrowserTree docxBrowserTree;

    private VirtualFile selectedFile;

    private ActionToolbar actionToolbar;

    private JBSplitter splitter;

    private DocBrowserPlugin myPlugin;

    public DocBrowserToolWindow(@NotNull Project project, DocBrowserPlugin plugin) {
        super(new BorderLayout());
        this.project = project;
        this.myPlugin = plugin;
        this.docxBrowserTree = new DocBrowserTree(project, plugin);
        initActionToolbar();
        initTreeSplitter();
    }

    private void initActionToolbar() {
        AnAction action = ActionManager.getInstance().getAction("DocxBrowser.Toolbar");
        actionToolbar = ActionManager.getInstance().createActionToolbar(
                ActionPlaces.TOOLBAR,
                action instanceof ActionGroup ? (ActionGroup) action : new DefaultActionGroup(),
                true
        );
        actionToolbar.setTargetComponent(this);
    }

    private void addActionToolbar() {
        this.add(actionToolbar.getComponent(), BorderLayout.NORTH);
    }

    private void removeActionToolbar() {
        this.remove(actionToolbar.getComponent());
    }

    private void initTreeSplitter() {
        splitter = new JBSplitter(true, DocBrowserTree.class.getName(), 0.5F);
        splitter.setFirstComponent(this.docxBrowserTree);
        splitter.setSecondComponent(null);
    }

    private void addTreeSplitter() {
        this.add(splitter, BorderLayout.CENTER);
    }

    private void removeTreeSplitter() {
        this.remove(splitter);
    }

    public void updateContent(VirtualFile virtualFile) {
        // already opened
        if (null != selectedFile && selectedFile.equals(virtualFile)) return;

        selectedFile = virtualFile;
        removeTreeSplitter();
        if (!DocBrowserUtil.isDocxFile(virtualFile)) {
            removeActionToolbar();
            docxBrowserTree.updateTreeWithNonDocx();
        } else {
            myPlugin.register(virtualFile);
            addActionToolbar();
            docxBrowserTree.updateTree(myPlugin.getFileHandler(selectedFile));
        }
        addTreeSplitter();
        repaint();
    }

    public void openCurrentDocxFile() {
        if (!selected()) return;
        myPlugin.flush(selectedFile);
        DocFileHandler fileHandler = myPlugin.getFileHandler(selectedFile);
        fileHandler.openTempFile();
    }

    public void saveChangesToDocxFile() {
        if (!selected()) return;
        myPlugin.flush(selectedFile);
        DocFileHandler fileHandler = myPlugin.getFileHandler(selectedFile);
        fileHandler.saveToDocFile();
    }

    private boolean selected() {
        return null != selectedFile && myPlugin.getFileHandlers().containsKey(selectedFile);
    }

    public void selectFileInTree(VirtualFile file) {
        docxBrowserTree.selectFile(file);
    }
}
