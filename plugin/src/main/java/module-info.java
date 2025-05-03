/**
 * @author VISTALL
 * @since 2025-04-21
 */
module com.intellij.jira {
    requires consulo.ide.api;

    requires com.intellij.jira.api;

    requires commons.httpclient;
    requires com.google.gson;
    requires org.slf4j;
    requires com.google.common;

    requires xmlrpc.common;
    requires xmlrpc.client;

    // TODO remove in future
    requires java.desktop;
    requires forms.rt;
    requires consulo.ui.ex.awt.api;
    requires miglayout;

    opens com.intellij.jira.actions to consulo.component.impl;
    opens com.intellij.jira.server to consulo.util.xml.serializer;
    opens com.intellij.jira.ui.table.column to consulo.util.xml.serializer;
}