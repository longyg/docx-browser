package com.yglong.plugin.intellij.listener;

import com.intellij.openapi.project.Project;
import com.yglong.plugin.intellij.service.DocBrowserToolWindowService;
import com.yglong.plugin.intellij.utils.DocBrowserUtil;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;

/**
 * @author longyg
 */
public class ProjectTreeSelectionListener implements TreeSelectionListener {
    private Project project;

    public ProjectTreeSelectionListener(Project project) {
        this.project = project;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DocBrowserUtil.consumeSelectedFile((Component) e.getSource(),
                file -> DocBrowserToolWindowService.getInstance(project).updateContent(file));
    }
}
