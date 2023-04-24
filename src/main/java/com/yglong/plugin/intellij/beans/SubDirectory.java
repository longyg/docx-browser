package com.yglong.plugin.intellij.beans;

import com.intellij.icons.AllIcons;

import javax.swing.*;

/**
 * @author longyg
 */
public class SubDirectory implements NodeData {
    private final String dirName;

    private final Icon icon;

    public SubDirectory(String dirName) {
        this.dirName = dirName;
        this.icon = AllIcons.Modules.SourceRoot;
    }

    public String getDirName() {
        return dirName;
    }

    public Icon getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return String.format("%s", dirName);
    }
}
