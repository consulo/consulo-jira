package com.intellij.jira.ui.panels;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.actions.AddWorklogDialogAction;
import com.intellij.jira.actions.DeleteWorklogDialogAction;
import com.intellij.jira.actions.EditWorklogDialogAction;
import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueTimeTracking;
import com.intellij.jira.rest.model.JiraIssueWorklog;
import com.intellij.jira.ui.model.JiraIssueWorklogListModel;
import com.intellij.jira.ui.renders.JiraIssueWorklogListCellRender;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.List;
import java.util.Objects;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

class JiraIssueWorkLogsPanel extends AbstractJiraToolWindowPanel {

    private JiraIssueWorklog worklog;
    private JiraIssueTimeTracking timeTracking;

    private JBList<JiraIssueWorklog> issueWorklogList;

    JiraIssueWorkLogsPanel(@NotNull JiraIssue issue) {
        super(issue);
        this.timeTracking = issue.getTimetracking();

        initContent(issue.getWorklogs());
    }

    @Override
    public ActionGroup getActionGroup() {
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(new AddWorklogDialogAction());
        group.add(new EditWorklogDialogAction());
        group.add(new DeleteWorklogDialogAction());

        return group;
    }

    @Override
    public @Nullable Object getData(@NotNull String dataId) {
        if (JiraDataKeys.ISSUE_WORKLOG.is(dataId)
                && Objects.nonNull(issueWorklogList.getSelectedValue())) {
            return worklog;
        } else if (JiraDataKeys.ISSUE_TIME_TRACKING.is(dataId)) {
            return timeTracking;
        }

        return super.getData(dataId);
    }

    private void initContent(List<JiraIssueWorklog> worklogs){
        JPanel panel = new JiraPanel(new BorderLayout());

        issueWorklogList = new JBList<>();
        issueWorklogList.setEmptyText("No work logs");
        issueWorklogList.setModel(new JiraIssueWorklogListModel(worklogs));
        issueWorklogList.setCellRenderer(new JiraIssueWorklogListCellRender());
        issueWorklogList.setSelectionMode(SINGLE_SELECTION);
        issueWorklogList.addListSelectionListener(e -> {
            ApplicationManager.getApplication().invokeLater(this::updateToolbarActions);
        });

        panel.add(ScrollPaneFactory.createScrollPane(issueWorklogList, VERTICAL_SCROLLBAR_AS_NEEDED), CENTER);

        setContent(panel);
    }

    private void updateToolbarActions() {
        JiraIssueWorklog selectedWorklog = issueWorklogList.getSelectedValue();
        if(!Objects.equals(worklog, selectedWorklog)){
            worklog = selectedWorklog;
        }
    }

}
