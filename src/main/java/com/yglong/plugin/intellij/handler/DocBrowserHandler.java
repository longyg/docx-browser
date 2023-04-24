package com.yglong.plugin.intellij.handler;

/**
 * @author longyg
 */
public interface DocBrowserHandler {
    void register(DocBrowserProjectHandler docBrowserProjectHandler);

    void unregister(DocBrowserProjectHandler docBrowserProjectHandler);
}
