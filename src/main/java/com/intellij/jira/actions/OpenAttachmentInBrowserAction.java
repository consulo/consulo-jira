package com.intellij.jira.actions;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.rest.model.JiraIssueAttachment;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.webBrowser.BrowserUtil;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.nonNull;

public class OpenAttachmentInBrowserAction extends AnAction {

    public OpenAttachmentInBrowserAction() {
        super("Open in Browser", "", PlatformIconGroup.nodesPpweb());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JiraIssueAttachment issueAttachment = e.getRequiredData(JiraDataKeys.ISSUE_ATTACHMENT);
        BrowserUtil.open(issueAttachment.getContent());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(nonNull(e.getData(JiraDataKeys.ISSUE_ATTACHMENT)));
    }
}
