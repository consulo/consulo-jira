package com.intellij.jira.ui.panels;

import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.JiraTabbedPane;
import com.intellij.jira.util.JiraPanelUtil;
import consulo.disposer.Disposable;
import consulo.disposer.Disposer;
import consulo.project.Project;
import consulo.ui.ex.awt.SimpleToolWindowPanel;
import consulo.util.dataholder.Key;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

public class JiraIssueDetailsPanel extends SimpleToolWindowPanel implements Disposable {

    public static final String TAB_KEY = "selectedTab";
    private static final String TAB_PREVIEW = " Preview ";
    public static final String TAB_ATTACHMENTS = " Attachments";
    public static final String TAB_LINKS = " Links";
    public static final String TAB_SUB_TASKS = " Sub-Tasks";
    public static final String TAB_COMMENTS = " Comments";
    public static final String TAB_WORK_LOG = " Work Log";

    private final Project myProject;
    private final Map<String, Integer> data = new HashMap<>();

    private JiraIssuePreviewPanel myJiraIssuePreviewPanel;
    private JiraIssueAttachmentsPanel myJiraIssueAttachmentsPanel;
    private JiraIssueLinksPanel myJiraIssueLinksPanel;
    private JiraIssueSubtasksPanel myJiraIssueSubtasksPanel;
    private JiraIssueCommentsPanel myJiraIssueCommentsPanel;
    private JiraIssueWorkLogsPanel myJiraIssueWorkLogsPanel;

    public JiraIssueDetailsPanel(@NotNull JiraIssuesData issuesData, @NotNull Disposable parent){
        super(true);

        Disposer.register(parent, this);

        this.myProject = issuesData.getProject();
        setEmptyContent();
    }

    public void showIssue(@Nullable JiraIssue issue) {
        if (isNull(issue)) {
            setEmptyContent();
        } else {

            myJiraIssuePreviewPanel = new JiraIssuePreviewPanel(myProject, issue);
            myJiraIssueAttachmentsPanel = new JiraIssueAttachmentsPanel(issue);
            myJiraIssueLinksPanel = new JiraIssueLinksPanel(issue);
            myJiraIssueSubtasksPanel = new JiraIssueSubtasksPanel(issue);
            myJiraIssueCommentsPanel = new JiraIssueCommentsPanel(issue);
            myJiraIssueWorkLogsPanel = new JiraIssueWorkLogsPanel(issue);

            JiraTabbedPane tabbedPane = new JiraTabbedPane(JTabbedPane.BOTTOM);
            tabbedPane.addTab(TAB_PREVIEW, myJiraIssuePreviewPanel);
            tabbedPane.addTab(TAB_ATTACHMENTS + appendTotal(issue.getAttachments().size()), myJiraIssueAttachmentsPanel);
            tabbedPane.addTab(TAB_LINKS + appendTotal(issue.getIssueLinks().size()), myJiraIssueLinksPanel);
            tabbedPane.addTab(TAB_SUB_TASKS + appendTotal(issue.getSubtasks().size()), myJiraIssueSubtasksPanel);
            tabbedPane.addTab(TAB_COMMENTS + appendTotal(issue.getRenderedComments().getTotal()), myJiraIssueCommentsPanel);
            tabbedPane.addTab(TAB_WORK_LOG + appendTotal(issue.getWorklogs().size()), myJiraIssueWorkLogsPanel);

            tabbedPane.addChangeListener(e -> data.put(TAB_KEY, tabbedPane.getSelectedIndex()));
            tabbedPane.setSelectedIndex(data.getOrDefault(TAB_KEY, 0));

            setContent(tabbedPane);
        }

    }

    public void setEmptyContent(){
        setContent(JiraPanelUtil.createPlaceHolderPanel("Select issue to view details"));
    }

    private String appendTotal(int total) {
        return total > 0 ? " (" + total + ") " : " ";
    }

    public void setToolbarHeightReferent(@NotNull JComponent referent) {
        myJiraIssuePreviewPanel.setToolbarHeightReferent(referent);
        myJiraIssueAttachmentsPanel.setToolbarHeightReferent(referent);
        myJiraIssueLinksPanel.setToolbarHeightReferent(referent);
        myJiraIssueCommentsPanel.setToolbarHeightReferent(referent);
        myJiraIssueWorkLogsPanel.setToolbarHeightReferent(referent);
    }

    @Override
    public @Nullable Object getData(@NotNull @NonNls Key dataId) {
        if (myJiraIssuePreviewPanel != null) {
            var data = myJiraIssuePreviewPanel.getData(dataId);
            if (data != null) {
                return data;
            }
        }

        return super.getData(dataId);
    }

    @Override
    public void dispose() {

    }
}
