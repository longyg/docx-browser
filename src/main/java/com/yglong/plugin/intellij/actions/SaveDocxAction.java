package com.yglong.plugin.intellij.actions;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.yglong.plugin.intellij.DocBrowserToolWindowFactory;
import com.yglong.plugin.intellij.service.DocBrowserToolWindowService;
import org.jetbrains.annotations.NotNull;

import static com.yglong.plugin.intellij.constants.Constants.DOCX;

/**
 * @author longyg
 */
public class SaveDocxAction extends AnAction {

    public SaveDocxAction() {
        getTemplatePresentation().setText("Save All Changes");
        getTemplatePresentation().setIcon(AllIcons.Actions.MenuSaveall);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DocBrowserToolWindowService.getInstance(e.getProject()).saveChangesToDocxFile();
        Notification notification = new Notification("Docx Browser", "Docx saved",
                "The docx file with all changes saved successfully", NotificationType.INFORMATION);
        Notifications.Bus.notify(notification);
    }
}