package com.intellij.jira.ui.panels;

import com.google.common.util.concurrent.SettableFuture;
import com.intellij.jira.actions.ConfigureJiraServersAction;
import com.intellij.jira.actions.GoToIssuePopupAction;
import com.intellij.jira.actions.JQLSearcherActionGroup;
import com.intellij.jira.actions.JiraIssueActionGroup;
import com.intellij.jira.components.JQLSearcherManager;
import com.intellij.jira.listener.JiraIssueChangeListener;
import com.intellij.jira.listener.JiraIssuesRefreshedListener;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.server.JiraRestApi;
import com.intellij.jira.ui.table.JiraIssueListTableModel;
import com.intellij.jira.ui.table.JiraIssueTableView;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;

import static com.intellij.jira.ui.JiraToolWindowFactory.TOOL_WINDOW_ID;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class JiraIssuesPanel extends SimpleToolWindowPanel {

    private final JiraRestApi myJiraRestApi;
    private final Project myProject;
    private final JQLSearcherManager myManager;

    private JiraIssueTableView issueTable;
    private JiraIssueDetailsPanel issueDetailsPanel;

    public JiraIssuesPanel(JiraRestApi server, Project project) {
        super(false, true);
        this.myJiraRestApi = server;
        this.myProject = project;
        this.myManager = JQLSearcherManager.getInstance();

        init();
        subscribeTopic();
    }

    private void subscribeTopic() {
        MessageBusConnection connection = myProject.getMessageBus().connect();

        connection.subscribe(JiraIssueChangeListener.TOPIC, issue -> {
            if(nonNull(issueTable)){
                JiraIssueListTableModel model = issueTable.getModel();
                int postItem = model.indexOf(issue);
                if(postItem < 0){
                    return;
                }

                model.removeRow(postItem);
                model.insertRow(postItem, issue);
                issueTable.addSelection(issue);
            }
        });

        connection.subscribe(JiraIssuesRefreshedListener.TOPIC, issues -> {
            if(nonNull(issueTable)){
                JiraIssue lastSelectedIssue = issueTable.getSelectedObject();
                ApplicationManager.getApplication().invokeLater(() -> {

                    issueTable.updateModel(issues);
                    int currentPosIssue = issueTable.getModel().indexOf(lastSelectedIssue);
                    // if the last selected issue exists in the new list, we select it
                    if(currentPosIssue >= 0) {
                        JiraIssue issueToShow = issueTable.getModel().getItem(currentPosIssue);
                        issueTable.addSelection(issueToShow);
                    } else {
                        issueDetailsPanel.setEmptyContent();
                    }
                });
            }
        });

    }

    private void init() {
        setToolbar();
        setContent();
    }

    private void setContent() {
        JComponent content;
        if(isNull(myJiraRestApi)) {
            content = JiraPanelUtil.createPlaceHolderPanel("No Jira server found");
        } else {
            List<JiraIssue> issues = myJiraRestApi.getIssues(getDefaultJQLSearcher());
            issueDetailsPanel = new JiraIssueDetailsPanel(myProject);

            issueTable = new JiraIssueTableView(issues);
            issueTable.getSelectionModel().addListSelectionListener(event ->  this.issueDetailsPanel.showIssue(issueTable.getSelectedObject()));

            JPanel issuesPanel = new JPanel(new BorderLayout());

            JPanel jqlPanel = new JiraPanel(new BorderLayout());
            jqlPanel.setBorder(JBUI.Borders.customLine(JBColor.border(),0, 0, 1, 0));
            jqlPanel.add(new JiraJQLSearcherPanel(myProject), BorderLayout.WEST);

            issuesPanel.add(jqlPanel, BorderLayout.PAGE_START);
            issuesPanel.add(ScrollPaneFactory.createScrollPane(issueTable), BorderLayout.CENTER);


            JBSplitter splitter = new OnePixelSplitter(0.6f);
            splitter.setFirstComponent(issuesPanel);
            splitter.setSecondComponent(issueDetailsPanel);

            content = splitter;
        }

        super.setContent(content);
    }

    private void setToolbar(){
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(TOOL_WINDOW_ID, createActionGroup(), false);
        actionToolbar.setTargetComponent(this);
        Box toolBarBox = Box.createHorizontalBox();
        toolBarBox.add(actionToolbar.getComponent());
        super.setToolbar(toolBarBox);
    }

    private ActionGroup createActionGroup(){
        JiraIssueActionGroup group = new JiraIssueActionGroup(this);
        group.add(ActionManager.getInstance().getAction("Jira.toolwindow.Refresh"));
        group.add(new JQLSearcherActionGroup());
        group.add(new GoToIssuePopupAction());
        group.add(Separator.getInstance());
        group.add(new ConfigureJiraServersAction());
        return group;
    }

    public JiraIssueListTableModel getTableListModel(){
        return issueTable.getModel();
    }

    public JiraIssueTableView getIssueTable(){
        return issueTable;
    }

    public Future<Boolean> goToIssue(String issueKey){
        SettableFuture<Boolean> future = SettableFuture.create();
        future.set(false);
        Optional<JiraIssue> targetIssue = issueTable.getItems().stream().filter(issue -> Objects.equals(issue.getKey(), issueKey)).findFirst();
        if(targetIssue.isPresent()){
            future.set(true);
            issueTable.addSelection(targetIssue.get());
            issueTable.scrollRectToVisible(issueTable.getCellRect(issueTable.getSelectedRow(),issueTable.getSelectedColumn(), true));
            issueDetailsPanel.showIssue(targetIssue.get());
        }

        return future;
    }

    private String getDefaultJQLSearcher(){
        return myManager.getSelectedSearcher(myProject).getJql();
    }

}
