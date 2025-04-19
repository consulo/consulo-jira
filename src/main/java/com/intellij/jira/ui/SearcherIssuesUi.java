package com.intellij.jira.ui;

import com.intellij.jira.JiraUiDataKeys;
import com.intellij.jira.actions.*;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.listener.SearcherListener;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.table.column.JiraIssueApplicationSettings;
import com.intellij.jira.ui.tree.SearcherTree;
import consulo.application.AllIcons;
import consulo.application.ApplicationManager;
import consulo.application.ui.UISettings;
import consulo.ui.ex.Gray;
import consulo.ui.ex.JBColor;
import consulo.ui.ex.action.ActionManager;
import consulo.ui.ex.action.ActionToolbar;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.DefaultActionGroup;
import consulo.ui.ex.awt.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
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

        myAppSettings = ApplicationManager.getApplication().getInjectingContainer(JiraIssueApplicationSettings.class);
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

        mySearchersButton = new MySearchersButton("Searchers",  AllIcons.Actions.ArrowExpand);

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

    @NotNull
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
        public @Nullable Object getData(@NotNull @NonNls String dataId) {
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
            setUI(new MyButtonUI());
            setOpaque(false);
            setFont(UIUtil.getLabelFont(UIUtil.FontSize.SMALL));
        }


        private class MyButtonUI extends BasicButtonUI {
            private Color HOVER_BACKGROUND_COLOR =
                    JBColor.namedColor("ToolWindow.Button.hoverBackground", new JBColor(Gray.x55.withAlpha(40), Gray.x0F.withAlpha(40)));

            private Rectangle myIconRect = new Rectangle();
            private Rectangle myTextRect = new Rectangle();
            private Rectangle myViewRect = new Rectangle();
            private Insets ourViewInsets = JBUI.emptyInsets();

            public Dimension getMinimumSize(JComponent c) { return getPreferredSize(c);}
            public Dimension getMaximumSize(JComponent c) { return getPreferredSize(c);}
            public Dimension getPreferredSize(JComponent c) { return ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE; }

            public void update(Graphics g, JComponent c) {
                var button = (MySearchersButton) c;
                var text = button.getText();
                var icon = (button.isEnabled()) ? button.getIcon() : button.getDisabledIcon();
                if (icon == null && text == null) {
                    return;
                }

                var fm = button.getFontMetrics(button.getFont());
                ourViewInsets = c.getInsets(ourViewInsets);
                myViewRect.x = ourViewInsets.left;
                myViewRect.y = ourViewInsets.top;

                // Use inverted height & width
                myViewRect.height = c.getWidth() - (ourViewInsets.left + ourViewInsets.right);
                myViewRect.width = c.getHeight() - (ourViewInsets.top + ourViewInsets.bottom);

                myIconRect.height = 0;
                myIconRect.width = myIconRect.height;
                myIconRect.y = myIconRect.width;
                myIconRect.x = myIconRect.y;
                myTextRect.height = 0;
                myTextRect.width = myTextRect.height;
                myTextRect.y = myTextRect.width;
                myTextRect.x = myTextRect.y;

                var clippedText = SwingUtilities.layoutCompoundLabel(
                        c, fm, text, icon,
                        SwingConstants.CENTER, SwingConstants.RIGHT, SwingConstants.CENTER, SwingConstants.TRAILING,
                        myViewRect, myIconRect, myTextRect,
                text == null ? 0 : button.getIconTextGap());

                // Paint button's background
                var g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    var model = button.model;
                    myIconRect.x -= JBUIScale.scale(2);
                    myTextRect.x -= JBUIScale.scale(2);

                    g2.setColor((model.isRollover() || model.isPressed()) ? HOVER_BACKGROUND_COLOR : c.getBackground());
                    g2.fillRect(0, 0, c.getWidth(), c.getHeight());

                    if (icon != null) {
                        icon.paintIcon(c, g2, myIconRect.y, JBUIScale.scale(2 * button.getIconTextGap()));
                    }

                    g2.rotate(-Math.PI / 2);
                    g2.translate(-c.getHeight() - 2 * myIconRect.width, 0);

                    // paint text
                    UISettings.setupAntialiasing(g2);
                    if (text != null) {
                        if (model.isEnabled()) {
                            /* paint the text normally */
                            g2.setColor(c.getForeground());
                        }
                        else {
                            /* paint the text disabled ***/
                            g2.setColor(UIManager.getColor("Button.disabledText"));
                        }
                        BasicGraphicsUtils.drawString(g2, clippedText, 0, myTextRect.x, myTextRect.y + fm.getAscent());
                    }
                }
                finally {
                    g2.dispose();
                }
            }
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
        public <T> void onChanged(JiraIssueUiProperties.@NotNull JiraIssueUiProperty<T> property) {
            if (SHOW_SEARCHERS_PROPERTY.equals(property)) {
                toggleSearchersPanel(myAppSettings.get(SHOW_SEARCHERS_PROPERTY));
            }
        }
    }
}
