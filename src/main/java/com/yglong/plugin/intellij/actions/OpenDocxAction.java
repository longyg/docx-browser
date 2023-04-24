package com.yglong.plugin.intellij.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.yglong.plugin.intellij.service.DocBrowserToolWindowService;
import org.jetbrains.annotations.NotNull;

/**
 * @author longyg
 */
public class OpenDocxAction extends DumbAwareAction {

    public OpenDocxAction() {
        getTemplatePresentation().setText("Open In Word");
        getTemplatePresentation().setIcon(AllIcons.Actions.Preview);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DocBrowserToolWindowService.getInstance(e.getProject()).openDocxFile();
    }
}
