package com.yglong.plugin.intellij.listener;

import com.intellij.ide.AppLifecycleListener;
import com.yglong.plugin.intellij.DocBrowserPlugin;

/**
 * @author longyg
 */
public class DocBrowserAppLifecycleListener implements AppLifecycleListener {

    @Override
    public void appWillBeClosed(boolean isRestart) {
        DocBrowserPlugin.getInstance().dispose();
    }
}
