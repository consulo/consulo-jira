package com.intellij.jira.ui;

import com.intellij.jira.JiraUiDataKeys;
import com.intellij.jira.actions.*;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.listener.SearcherListener;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.table.column.JiraIssueApplicationSettings;
import com.intellij.jira.ui.tree.SearcherTree;
import consulo.application.ApplicationManager;
import consulo.dataContext.DataProvider;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.ex.JBColor;
import consulo.ui.ex.action.ActionManager;
import consulo.ui.ex.action.ActionToolbar;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.DefaultActionGroup;
import consulo.ui.ex.awt.*;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import consulo.util.dataholder.Key;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import java.awt.*;

import static consulo.ui.ex.awt.IdeBorderFactory.createBorder;

public class SearcherIssuesUi extends DefaultIssuesUi {

    public static final ShowSearchersProperty SHOW_SEARCHERS_PROPERTY = new ShowSearchersProperty();

    private SearcherSplitter mySearcherSplitter;

    private MyExpandablePanelController myExpandablePanelController;
    private MySearchersButton mySearchersButton;

    private BorderLayoutPanel mySearcherPanel;
    private BorderLayoutPanel mySearchersWithIssuesPanel;

    private SearcherTree myTree;

    private JiraIssueApplicationSettings myAppSettings;
    private JiraIssueUiProperties.PropertyChangeListener myShowSearchersListener;

    public SearcherIssuesUi(JiraIssuesData issuesData) {
        super(issuesData);

        myAppSettings = ApplicationManager.getApplication().getInstance(JiraIssueApplicationSettings.class);
        myShowSearchersListener = new MyPropertyChangeListener();
        myAppSettings.addChangeListener(myShowSearchersListener);

        mySearcherPanel = new MySearcherPanel();


        myTree = new SearcherTree(issuesData.getProject());

        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(myTree, true);

        mySearcherPanel.addToCenter(scrollPane);

        ActionManager actionManager = ActionManager.getInstance();
        AnAction hideSearchersAction = actionManager.getAction("Jira.Issues.Hide.Searchers");

        DefaultActionGroup group = new DefaultActionGroup();
        group.add(hideSearchersAction);
        group.addSeparator();
        group.add(new AddSearcherAction());
        group.add(new DeleteSearcherAction());
        group.add(new EditSearcherAction());
        group.add(new OpenNewIssuesTabAction());
        group.add(new MakeSearcherProjectAction());
        group.add(new MakeSearcherGlobalAction());

        ActionToolbar toolbar = actionManager.createActionToolbar("Jira.Issues.Searchers", group, false);
        toolbar.setTargetComponent(mySearcherPanel);

        mySearchersButton = new MySearchersButton("Searchers", TargetAWT.to(PlatformIconGroup.generalArrowright()));

        myExpandablePanelController = new MyExpandablePanelController(toolbar.getComponent(), mySearchersButton, mySearcherPanel);

        mySearchersButton.addActionListener(e -> {
            if (myAppSettings.exists(SHOW_SEARCHERS_PROPERTY)) {
                myAppSettings.set(SHOW_SEARCHERS_PROPERTY, true);
            }
        });

        mySearcherSplitter = new SearcherSplitter();
        mySearcherSplitter.setFirstComponent(mySearcherPanel);
        mySearcherSplitter.setSecondComponent(myIssuesPanel);


        mySearchersWithIssuesPanel = new BorderLayoutPanel()
                                        .addToLeft(myExpandablePanelController.getExpandControlPanel())
                                        .addToCenter(mySearcherSplitter);

        toggleSearchersPanel(myAppSettings.get(SHOW_SEARCHERS_PROPERTY));

        issuesData.getProject().getMessageBus().connect()
                .subscribe(JQLSearcherManager.JQL_SEARCHERS_CHANGE, new MySearcherListener());
    }

    @Nonnull
    @Override
    public JComponent getMainComponent() {
        return mySearchersWithIssuesPanel;
    }

    @Override
    public void dispose() {
        super.dispose();
        myAppSettings.removeChangeListener(myShowSearchersListener);
    }

    public void toggleSearchersPanel(boolean show) {
        myExpandablePanelController.toggleExpand(show);
    }

    public static class ShowSearchersProperty extends JiraIssueUiProperties.JiraIssueUiProperty<Boolean> {

        public ShowSearchersProperty() {
            super("Searchers.show");
        }
    }


    private class MySearcherPanel extends BorderLayoutPanel implements DataProvider {
        public MySearcherPanel() {
            setBackground(JBColor.WHITE);
            setBorder(createBorder(JBColor.border(), SideBorder.LEFT));
        }

        @Override
        public @Nullable Object getData(@Nonnull @NonNls Key dataId) {
            if (JiraUiDataKeys.SEARCHER_TREE_NODE.is(dataId)) {
                return myTree.getSelectedNode();
            } else if (JiraUiDataKeys.JIRA_UI_PROPERTIES.is(dataId)) {
                return myAppSettings;
            }

            return null;
        }

    }

    private class SearcherSplitter extends OnePixelSplitter {

        public SearcherSplitter() {
            super(false, "searcher.issues.proportion",0.3f);
        }
    }

    private class MySearchersButton extends JButton {


        public MySearchersButton(String text, Icon icon) {
            super(icon);

            setText(text);
            setRolloverEnabled(true);
            setBorder(new SideBorder(JBColor.border(), SideBorder.RIGHT));
        }

        @Override
        public void updateUI() {
            super.updateUI();
            setOpaque(false);
            setFont(UIUtil.getLabelFont(UIUtil.FontSize.SMALL));
        }
    }

    private class MyExpandablePanelController {

        private final JComponent myExpandablePanel;
        private JPanel myExpandControlPanel;

        @NonNls
        private String EXPAND = "expand";
        @NonNls private String COLLAPSE = "collapse";

        public MyExpandablePanelController(JComponent expandedControlContent, JComponent collapsedControlContent, JComponent expandablePanel) {
            myExpandablePanel = expandablePanel;

            myExpandControlPanel = new JPanel(new CardLayout());
            Wrapper collapsedWrapped = new Wrapper(collapsedControlContent);
            Wrapper expandedWrapped = new Wrapper(expandedControlContent);
            collapsedWrapped.setHorizontalSizeReferent(expandedWrapped);
            collapsedWrapped.setVerticalSizeReferent(expandedWrapped);
            myExpandControlPanel.add(collapsedWrapped, COLLAPSE);
            myExpandControlPanel.add(expandedWrapped, EXPAND);
        }

        public JPanel getExpandControlPanel() {
            return myExpandControlPanel;
        }

        public boolean isExpanded() {
            return myExpandablePanel.isVisible();
        }

        public void toggleExpand(boolean expand) {
            ((CardLayout) myExpandControlPanel.getLayout()).show(myExpandControlPanel, expand ? EXPAND : COLLAPSE);
            myExpandablePanel.setVisible(expand);
        }
    }

    private class MySearcherListener implements SearcherListener {

        @Override
        public void onAdded(JQLSearcher editedSearcher) {
            myTree.update();
        }

        @Override
        public void onChange(JQLSearcher editedSearcher) {
            // update tree
            myTree.update();
        }

        @Override
        public void onRemoved(JQLSearcher removedSearcher) {
            // update tree
            myTree.update();
        }
    }

    private class MyPropertyChangeListener implements JiraIssueUiProperties.PropertyChangeListener {

        @Override
        public <T> void onChanged(JiraIssueUiProperties.JiraIssueUiProperty<T> property) {
            if (SHOW_SEARCHERS_PROPERTY.equals(property)) {
                toggleSearchersPanel(myAppSettings.get(SHOW_SEARCHERS_PROPERTY));
            }
        }
    }
}
