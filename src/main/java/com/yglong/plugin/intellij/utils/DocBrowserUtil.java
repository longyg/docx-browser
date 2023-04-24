package com.yglong.plugin.intellij.utils;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.yglong.plugin.intellij.constants.Constants.DOCX;

/**
 * @author longyg
 */
public class DocBrowserUtil {
    public static void invokeSafe(final Project project, final Runnable runnable) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (isValid(project)) {
                runnable.run();
            }
        });
    }

    public static void consumeDataContext(final Component component, final Consumer<DataContext> dataContextConsumer) {
        DataContext dataContext = DataManager.getInstance().getDataContext(component);
        getReady(dataContext, dataContextConsumer).doWhenDone(() -> ApplicationManager.getApplication().invokeLater(() -> {
            dataContextConsumer.accept(DataManager.getInstance().getDataContext(component));
        }));
    }

    public static void consumeSelectedFile(final Component tree, Consumer<VirtualFile> consumer) {
        consumeDataContext(tree, context -> consumer.accept(getFileFromDataContext(context)));
    }

    private static ActionCallback getReady(DataContext context, Object requester) {
        ToolWindow toolWindow = PlatformDataKeys.TOOL_WINDOW.getData(context);
        return toolWindow != null ? toolWindow.getReady(requester) : ActionCallback.DONE;
    }

    public static VirtualFile getFileFromDataContext(@NotNull final DataContext dataContext) {
        return CommonDataKeys.VIRTUAL_FILE.getData(dataContext);
    }

    public static VirtualFile getGotoFile(final Project project, final VirtualFile file) {
        if (!isValid(project) || file == null) {
            return null;
        }

        PsiElement element = PsiManager.getInstance(project).findFile(file);
        if (element != null) {
            PsiElement navElement = element.getNavigationElement();
            navElement = TargetElementUtil.getInstance().getGotoDeclarationTarget(element, navElement);
            if (navElement != null && navElement.getContainingFile() != null) {
                return navElement.getContainingFile().getVirtualFile();
            }
        }
        return file;
    }

    public static boolean isValid(Project project) {
        return project != null && !project.isDisposed();
    }

    public static boolean isDocxFile(VirtualFile virtualFile) {
        return null != virtualFile && !virtualFile.isDirectory() && virtualFile.getName().endsWith(DOCX);
    }

    public static void zip(File zipFile, File srcFile, String dir) {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zip(zos, srcFile, dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void zip(ZipOutputStream zos, File srcFile, String dir) throws Exception {
        if (srcFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            if (null == files) return;
            zos.putNextEntry(new ZipEntry(dir + "/"));
            dir = dir.length() == 0 ? "" : dir + "/";
            for (File file : files) {
                zip(zos, file, dir + file.getName());
            }
        } else {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile))) {
                ZipEntry zipEntry = new ZipEntry(dir);
                zos.putNextEntry(zipEntry);
                zos.write(bis.readAllBytes());
                zos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
