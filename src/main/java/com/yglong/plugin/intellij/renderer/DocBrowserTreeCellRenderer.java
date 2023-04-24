package com.yglong.plugin.intellij.renderer;

import com.intellij.ui.ColoredTreeCellRenderer;
import com.yglong.plugin.intellij.beans.SubDirectory;
import com.yglong.plugin.intellij.beans.SubFile;
import com.yglong.plugin.intellij.beans.RootFile;
import com.yglong.plugin.intellij.toolwindow.DocBrowserTree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author longyg
 */
public class DocBrowserTreeCellRenderer extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected,
                                      boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof DocBrowserTree.RootNode rootNode) {
            RootFile data = rootNode.getData();
            setIcon(data.getIcon());
            append(data.toString());
        } else if (value instanceof DocBrowserTree.DirectoryNode directoryNode) {
            SubDirectory data = directoryNode.getData();
            setIcon(data.getIcon());
            append(data.toString());
        } else if (value instanceof DocBrowserTree.FileNode fileNode) {
            SubFile data = fileNode.getData();
            setIcon(data.getIcon());
            append(data.getName());
        } else if (value instanceof DocBrowserTree.TreeNode<?> node) {
            append(node.toString());
        }
    }
}
