package com.yglong.plugin.intellij.beans;

import com.intellij.icons.AllIcons;

import javax.swing.*;

/**
 * @author longyg
 */
public class RootFile implements NodeData {
    private final String name;

    public RootFile(String name) {
        this.name = name;
    }

    public Icon getIcon() {
        return AllIcons.Modules.ResourcesRoot;
    }

    @Override
    public String toString() {
        return String.format("%s", name);
    }
}
