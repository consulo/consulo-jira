/**
 * @author VISTALL
 * @since 2025-04-21
 */
module com.intellij.jira.api {
    requires consulo.datacontext.api;
    requires consulo.localize.api;
    requires consulo.project.ui.api;

    exports consulo.jira.notification;
    exports consulo.jira.vcs;
}
