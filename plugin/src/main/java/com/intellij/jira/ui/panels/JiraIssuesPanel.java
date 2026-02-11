package com.intellij.jira.ui.panels;

import com.google.common.util.concurrent.SettableFuture;
import com.intellij.jira.JiraUiDataKeys;
import com.intellij.jira.actions.OpenIssueTaskAction;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.data.JiraProgress;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.ui.AbstractIssuesUi;
import com.intellij.jira.ui.JiraIssueActionPlaces;
import com.intellij.jira.ui.table.JiraIssueListTableModel;
import com.intellij.jira.ui.table.JiraIssueTable;
import com.intellij.jira.ui.table.column.JiraIssueApplicationSettings;
import consulo.application.ApplicationManager;
import consulo.dataContext.DataProvider;
import consulo.disposer.Disposable;
import consulo.disposer.Disposer;
import consulo.ui.ex.action.ActionManager;
import consulo.ui.ex.action.ActionToolbar;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.DefaultActionGroup;
import consulo.ui.ex.awt.OnePixelSplitter;
import consulo.ui.ex.awt.ProgressStripe;
import consulo.ui.ex.awt.ScrollPaneFactory;
import consulo.ui.ex.awt.Splitter;
import consulo.util.dataholder.Key;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NonNls;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;

public class JiraIssuesPanel extends JiraPanel implements DataProvider, Disposable {

    private final JComponent myToolbar;
    private final JiraIssueTable myJiraIssueTable;
    private final JiraIssueDetailsPanel myJiraIssueDetailsPanel;
    private final ProgressStripe myProgressStripe;
    private final AbstractIssuesUi myUi;

    private final Splitter myIssuesBrowserSplitter;

    public JiraIssuesPanel(@Nonnull JiraIssuesData issuesData, @Nonnull AbstractIssuesUi issuesUi, @Nonnull Disposable parent) {
        super(new BorderLayout());

        Disposer.register(parent, this);

        myUi = issuesUi;
        myToolbar = getToolbar();

        myJiraIssueDetailsPanel = new JiraIssueDetailsPanel(issuesData, parent);

        myJiraIssueTable = new JiraIssueTable(issuesData, parent);

        // setup handlers
        myJiraIssueTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 // double click
                        && e.getButton() == MouseEvent.BUTTON1 // by left mouse button
                        && myJiraIssueTable.getSelectedRow() != -1 // on some row
                ) {
                    invokeOpenTaskAction(e, myJiraIssueDetailsPanel);
                }
            }
        });

        myJiraIssueTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER // enter pressed
                        && myJiraIssueTable.getSelectedRow() != -1 // on some row
                ) {
                    invokeOpenTaskAction(e, myJiraIssueDetailsPanel);
                }
            }
        });


        myJiraIssueTable.getSelectionModel().addListSelectionListener(new MyListSelectionListener());

        JComponent toolbarAndTable = new JPanel(new BorderLayout());
        toolbarAndTable.add(myToolbar, getToolbarOrientation());
        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(myJiraIssueTable, true);
        myProgressStripe = new ProgressStripe(scrollPane, this, 1);

        toolbarAndTable.add(myProgressStripe, BorderLayout.CENTER);

        myIssuesBrowserSplitter = new OnePixelSplitter(0.6f);
        myIssuesBrowserSplitter.setFirstComponent(toolbarAndTable);
        myIssuesBrowserSplitter.setSecondComponent(myJiraIssueDetailsPanel);

        myUi.getProgress().addProgressListener(new MyProgressListener(), this);

        add(myIssuesBrowserSplitter);
    }

    private static void invokeOpenTaskAction(@Nonnull InputEvent e, @Nonnull Component component) {
        AnAction action = ActionManager.getInstance().getAction(OpenIssueTaskAction.ID);
        // TODO !!! ActionUtil.invokeAction(action, component, "JiraIssuesTable", e, null);
    }

    @Nonnull
    protected String getToolbarOrientation() {
        return BorderLayout.NORTH;
    }

    protected void setToolbarHeightReference() {
        myJiraIssueDetailsPanel.setToolbarHeightReferent(myToolbar);
    }

    @Override
    public @Nullable Object getData(@Nonnull @NonNls Key dataId) {
        if (JiraUiDataKeys.ISSUES_PANEL.is(dataId)) {
            return this;
        } else if (JiraUiDataKeys.JIRA_UI_PROPERTIES.is(dataId)) {
            return ApplicationManager.getApplication().getInstance(JiraIssueApplicationSettings.class);
        } else if (JiraUiDataKeys.ISSUES_UI.is(dataId)) {
            return myUi;
        }

        return null;
    }

    @Nonnull
    protected JComponent getToolbar() {
        DefaultActionGroup toolbarGroup = new DefaultActionGroup();
        toolbarGroup.copyFromGroup((DefaultActionGroup) ActionManager.getInstance().getAction(JiraIssueActionPlaces.JIRA_ISSUES_TOOLBAR_LEFT));

        DefaultActionGroup mainGroup = new DefaultActionGroup();
        mainGroup.add(myUi.getFilterUi().createActionGroup());
        mainGroup.addSeparator();
        mainGroup.add(toolbarGroup);

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(JiraIssueActionPlaces.JIRA_ISSUES_TOOLBAR_PLACE, mainGroup, true);
        toolbar.setTargetComponent(this);

        DefaultActionGroup rightCornerGroup =
                new DefaultActionGroup(ActionManager.getInstance().getAction(JiraIssueActionPlaces.JIRA_ISSUES_TOOLBAR_RIGHT));
        ActionToolbar rightCornerToolbar = ActionManager.getInstance().createActionToolbar(JiraIssueActionPlaces.JIRA_ISSUES_TOOLBAR_PLACE,
                rightCornerGroup, true);
        rightCornerToolbar.setTargetComponent(this);
        rightCornerToolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);

        JPanel panel = new JPanel(new MigLayout("ins 0, fill", "[left]push[pref:pref, right]", "center"));

        panel.add(toolbar.getComponent());
        panel.add(rightCornerToolbar.getComponent());

        return panel;
    }

    public JiraIssueTable getJiraIssueTable() {
        return myJiraIssueTable;
    }

    public Future<Boolean> goToIssue(String issueKey) {
        SettableFuture<Boolean> future = SettableFuture.create();
        future.set(false);
        Optional<JiraIssue> targetIssue = myJiraIssueTable.getItems().stream().filter(issue -> Objects.equals(issue.getKey(), issueKey)).findFirst();
        if(targetIssue.isPresent()){
            future.set(true);
            myJiraIssueTable.addSelection(targetIssue.get());
            myJiraIssueTable.scrollRectToVisible(myJiraIssueTable.getCellRect(myJiraIssueTable.getSelectedRow(), myJiraIssueTable.getSelectedColumn(), true));
            myJiraIssueDetailsPanel.showIssue(targetIssue.get());
        }

        return future;
    }

    @Override
    public void dispose() {

    }

    public void setIssues(List<JiraIssue> issues) {
        if (issues.isEmpty()) {
            myJiraIssueTable.getEmptyText().setText("No issues to display");
        }

        JiraIssue lastSelectedIssue = myJiraIssueTable.getSelectedObject();
        myJiraIssueTable.setModelAndUpdateColumns(new JiraIssueListTableModel());
        myJiraIssueTable.getModel().setIssues(issues);

        int currentPosIssue = myJiraIssueTable.getModel().indexOf(lastSelectedIssue);
        // if the last selected issue exists in the new list, we select it
        if (currentPosIssue >= 0) {
            JiraIssue issueToShow = myJiraIssueTable.getModel().getItem(currentPosIssue);
            myJiraIssueTable.addSelection(issueToShow);
        }
    }

    private class MyListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            myJiraIssueDetailsPanel.showIssue(myJiraIssueTable.getSelectedObject());
            setToolbarHeightReference();
        }
    }

    private class MyProgressListener implements JiraProgress.JiraProgressListener {

        @Override
        public void start() {
            myProgressStripe.startLoading();
            myJiraIssueTable.getEmptyText().setText("Loading issues...");
        }

        @Override
        public void finish() {
            myProgressStripe.stopLoading();
        }
    }

}
