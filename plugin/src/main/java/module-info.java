/**
 * @author VISTALL
 * @since 2025-04-21
 */
module com.intellij.jira {
    requires com.intellij.jira.api;

    requires consulo.application.api;
    requires consulo.code.editor.api;
    requires consulo.color.scheme.api;
    requires consulo.component.api;
    requires consulo.credential.storage.api;
    requires consulo.datacontext.api;
    requires consulo.disposer.api;
    requires consulo.http.api;
    requires consulo.language.api;
    requires consulo.language.editor.api;
    requires consulo.language.editor.ui.api;
    requires consulo.language.impl;
    requires consulo.localize.api;
    requires consulo.logging.api;
    requires consulo.base.icon.library;
    requires consulo.project.api;
    requires consulo.project.ui.api;
    requires consulo.proxy;
    requires consulo.task.api;
    requires consulo.ui.api;
    requires consulo.ui.ex.api;
    requires consulo.ui.ex.awt.api;
    requires consulo.util.collection;
    requires consulo.util.dataholder;
    requires consulo.util.io;
    requires consulo.util.lang;
    requires consulo.util.xml.serializer;
    requires consulo.version.control.system.api;
    requires consulo.virtual.file.system.api;
    requires consulo.web.browser.api;

    requires commons.httpclient;
    requires com.google.gson;
    requires org.slf4j;
    requires com.google.common;

    requires xmlrpc.common;
    requires xmlrpc.client;

    // TODO remove in future
    requires java.desktop;
    requires forms.rt;
    requires miglayout;

    opens com.intellij.jira.actions to consulo.component.impl;
    opens com.intellij.jira.server to consulo.util.xml.serializer;
    opens com.intellij.jira.ui.table.column to consulo.util.xml.serializer;
    opens com.intellij.jira.settings to consulo.util.xml.serializer;
    opens com.intellij.jira.settings.branch to consulo.util.xml.serializer;
}
