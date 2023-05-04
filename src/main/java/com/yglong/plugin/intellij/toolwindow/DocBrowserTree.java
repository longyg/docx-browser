package com.yglong.plugin.intellij.toolwindow;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import com.yglong.plugin.intellij.DocBrowserPlugin;
import com.yglong.plugin.intellij.beans.RootFile;
import com.yglong.plugin.intellij.beans.SubDirectory;
import com.yglong.plugin.intellij.beans.SubFile;
import com.yglong.plugin.intellij.beans.SubFileType;
import com.yglong.plugin.intellij.handler.DocFileHandler;
import com.yglong.plugin.intellij.renderer.DocBrowserTreeCellRenderer;
import com.yglong.plugin.intellij.utils.DocBrowserUtil;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author longyg
 */
public class DocBrowserTree extends JBScrollPane {
    private final Project project;
    private final Tree tree;
    private final DocBrowserPlugin myPlugin;

    private TreeSelectionListener selectionListener = (e) -> openTreePath(e.getPath());
    private MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                openTreePath(tree.getSelectionPath());
            }
        }
    };

    public DocBrowserTree(Project project, DocBrowserPlugin plugin) {
        this.project = project;
        this.myPlugin = plugin;
        this.tree = new SimpleTree();

        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setBorder(new CustomLineBorder(JBUI.insetsTop(1)));

        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.setRoot(new DefaultMutableTreeNode());
        tree.setCellRenderer(new DocBrowserTreeCellRenderer());
        tree.setRootVisible(true);
        tree.setShowsRootHandles(false);
        this.setViewportView(tree);
    }

    private void addTreeListeners() {
        tree.addTreeSelectionListener(selectionListener);
        tree.addMouseListener(mouseListener);
    }

    private void removeTreeListeners() {
        tree.removeTreeSelectionListener(selectionListener);
        tree.removeMouseListener(mouseListener);
    }

    private void openTreePath(TreePath treePath) {
        if (null == treePath) return;
        Object selectedNode = treePath.getLastPathComponent();
        if (selectedNode instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) selectedNode;
            Object userObject = treeNode.getUserObject();
            if (userObject instanceof SubFile) {
                SubFile subFile = (SubFile) userObject;
                openSelectedSubFile(subFile);
            }
        }
    }

    private void openSelectedSubFile(SubFile subFile) {
        if (null == subFile) return;
        File tempFile = subFile.getTempFile();
        if (null == tempFile || !tempFile.exists()) return;

        VirtualFile tempVirtualFile = LocalFileSystem.getInstance().findFileByIoFile(tempFile);
        if (null == tempVirtualFile) return;

        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, tempVirtualFile);

        Editor editor = FileEditorManager.getInstance(project).openTextEditor(descriptor, true);
        if (null != editor) {
            Document document = editor.getDocument();
            document.setReadOnly(false);
            myPlugin.registerFileSubDocument(subFile.getDocFile(), document);
            myPlugin.registerSubFile(tempVirtualFile, subFile.getDocFile());
        }
        DocBrowserUtil.invokeSafe(project, () -> reformat(tempVirtualFile));
    }

    private void reformat(VirtualFile virtualFile) {
        if (virtualFile != null) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile != null && "XML".equals(psiFile.getFileType().getName())) {
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    CodeStyleManager.getInstance(project).reformat(psiFile, false);
                });
            }
        }
    }

    public void updateTreeWithNonDocx() {
        removeTreeListeners();
        ((DefaultTreeModel) tree.getModel()).setRoot(new TreeNode<>("No docx file selected"));
    }

    public void updateTree(DocFileHandler fileHandler) {
        removeTreeListeners();
        TreeNode<?> root = createTree(fileHandler);
        ((DefaultTreeModel) tree.getModel()).setRoot(root);
        addTreeListeners();
    }

    private TreeNode<?> createTree(DocFileHandler fileHandler) {
        VirtualFile file = fileHandler.getDocFile();
        String basePath = file.getPath() + "!/";
        RootNode root = new RootNode(new RootFile(file.getName()));
        try (ZipFile zipFile = new ZipFile(file.getPath())) {
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                String filePath = entry.getName();
                String[] path = entry.getName().split("/");
                TreeNode<?> node = root;
                int lastDirIndex = entry.isDirectory() ? path.length : path.length - 1;
                for (int i = 0; i < lastDirIndex; i++) {
                    String dirName = path[i];
                    if (dirName.isEmpty()) continue;
                    TreeNode<?> child = node.getChild(dirName);
                    if (null == child) {
                        child = new DirectoryNode(new SubDirectory(dirName));
                        child.setName(dirName);
                        node.add(child);
                        node.addChild(child);
                    }
                    node = child;
                }
                if (!entry.isDirectory()) {
                    String name = path[lastDirIndex];
                    FileNode fileNode = new FileNode(
                            new SubFile(
                                    fileHandler,
                                    SubFileType.XML,
                                    name, basePath, filePath,
                                    zipFile.getInputStream(entry)));
                    fileNode.setName(name);
                    node.add(fileNode);
                    node.addChild(fileNode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new TreeNode<>("The selected .docx file is not a valid word document");
        }
        return root;
    }

    public void selectFile(VirtualFile file) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        TreePath path = findPath(root, file);
        if (null != path) {
            tree.expandPath(path);
            tree.setSelectionPath(path);
        }
    }

    private TreePath findPath(DefaultMutableTreeNode node, VirtualFile file) {
        if (node instanceof FileNode) {
            FileNode fileNode = (FileNode) node;
            Object userObject = fileNode.getUserObject();
            if (userObject instanceof SubFile) {
                SubFile subFile = (SubFile) userObject;
                File tempFile = subFile.getTempFile();
                VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(tempFile);
                if (null != virtualFile && virtualFile.equals(file)) {
                    return new TreePath(node.getPath());
                }
            }
        }
        Enumeration<javax.swing.tree.TreeNode> enumeration = node.children();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) enumeration.nextElement();
            TreePath treePath = findPath(child, file);
            if (treePath != null) {
                return treePath;
            }
        }
        return null;
    }


    public static class TreeNode<T> extends DefaultMutableTreeNode {
        public TreeNode(T data) {
            super(data);
            this.data = data;
        }

        private T data;
        private List<TreeNode<?>> children = new ArrayList<>();
        private String name;

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public List<TreeNode<?>> getChildren() {
            return children;
        }

        public void setChildren(List<TreeNode<?>> children) {
            this.children = children;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public TreeNode<?> getChild(String name) {
            for (TreeNode<?> child : children) {
                if (name.equals(child.getName())) {
                    return child;
                }
            }
            return null;
        }

        public void addChild(TreeNode<?> child) {
            children.add(child);
        }

        @Override
        public String toString() {
            return data.toString();
        }
    }

    public static class RootNode extends TreeNode<RootFile> {
        public RootNode(@NotNull RootFile data) {
            super(data);
        }
    }

    public static class DirectoryNode extends TreeNode<SubDirectory> {

        public DirectoryNode(@NotNull SubDirectory data) {
            super(data);
        }
    }

    public static class FileNode extends TreeNode<SubFile> {

        public FileNode(@NotNull SubFile data) {
            super(data);
        }
    }
}
