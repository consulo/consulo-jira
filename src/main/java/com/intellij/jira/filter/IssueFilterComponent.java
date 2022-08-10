package com.intellij.jira.filter;

import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.ClickListener;
import com.intellij.ui.popup.PopupState;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.StartupUiUtil;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.accessibility.AccessibleContextDelegate;
import com.intellij.vcs.log.VcsLogBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Supplier;

public abstract class IssueFilterComponent<Filter extends IssueFilter, Model extends FilterModel<Filter>> extends JPanel {

    protected static final Supplier<@Nls String> ALL = VcsLogBundle.messagePointer("vcs.log.filter.all");

    private static final int GAP_BEFORE_ARROW = 2;
    protected static final int BORDER_SIZE = 2;

    private final PopupState<JBPopup> myPopupState = PopupState.forPopup();
    @NotNull private final Supplier<@NlsContexts.Label String> myDisplayName;
    private JLabel myNameLabel;
    private JLabel myValueLabel;

    @NotNull protected final Model myFilterModel;

    protected IssueFilterComponent(@NotNull Supplier<@NlsContexts.Label String> displayName, @NotNull Model filterModel) {
        myDisplayName = displayName;
        myFilterModel = filterModel;
    }

    public JComponent initUi() {
        myNameLabel = new DynamicLabel(() -> myDisplayName.get() + ": ");
        myValueLabel = new DynamicLabel(this::getCurrentText);

        setDefaultForeground();
        setFocusable(true);
        setBorder(wrapBorder(createUnfocusedBorder()));

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        if (myNameLabel != null) add(myNameLabel);
        add(myValueLabel);
        add(Box.createHorizontalStrut(GAP_BEFORE_ARROW));
        add(new JLabel(AllIcons.Actions.FindAndShowNextMatchesSmall));

        installChangeListener(() -> {
            myValueLabel.revalidate();
            myValueLabel.repaint();
        });

        showPopupMenuOnClick();
        showPopupMenuFromKeyboard();
        if (shouldIndicateHovering()) {
            indicateHovering();
        }

        return this;
    }


    public String getCurrentText() {
        Filter filter = myFilterModel.getFilter();
        return filter == null || StringUtil.isEmpty(filter.getDisplayText()) ? ALL.get() : getText(filter);
    }

    protected abstract String getText(@NotNull Filter filter);

    public void installChangeListener(@NotNull Runnable onChange) {
        myFilterModel.addSetFilterListener(onChange);
    }

    @NotNull
    protected Color getDefaultSelectorForeground() {
        return StartupUiUtil.isUnderDarcula() ? UIUtil.getLabelForeground() : UIUtil.getInactiveTextColor().darker().darker();
    }

    protected boolean shouldIndicateHovering() {
        return true;
    }

    /**
     * Create popup actions available under this filter.
     */
    protected abstract ActionGroup createActionGroup();

    @NotNull
    protected AnAction createAllAction() {
        return new AllAction();
    }

    private class AllAction extends DumbAwareAction {

        AllAction() {
            super(ALL);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            myFilterModel.setFilter(null);
        }
    }

    private void showPopupMenuFromKeyboard() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(@NotNull KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    showPopupMenu();
                }
            }
        });
    }

    private void showPopupMenuOnClick() {
        new ClickListener() {
            @Override
            public boolean onClick(@NotNull MouseEvent event, int clickCount) {
                showPopupMenu();
                return true;
            }
        }.installOn(this);
    }

    private void indicateHovering() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(@NotNull MouseEvent e) {
                setOnHoverForeground();
            }

            @Override
            public void mouseExited(@NotNull MouseEvent e) {
                setDefaultForeground();
            }
        });
    }

    private void setDefaultForeground() {
        if (myNameLabel != null) {
            myNameLabel.setForeground(StartupUiUtil.isUnderDarcula() ? UIUtil.getLabelForeground() : UIUtil.getInactiveTextColor());
        }
        myValueLabel.setForeground(getDefaultSelectorForeground());
    }

    private void setOnHoverForeground() {
        if (myNameLabel != null) {
            myNameLabel.setForeground(StartupUiUtil.isUnderDarcula() ? UIUtil.getLabelForeground() : UIUtil.getTextAreaForeground());
        }
        myValueLabel.setForeground(StartupUiUtil.isUnderDarcula() ? UIUtil.getLabelForeground() : UIUtil.getTextFieldForeground());
    }

    public void showPopupMenu() {
        if (myPopupState.isRecentlyHidden()) return; // do not show new popup
        ListPopup popup = createPopupMenu();
        myPopupState.prepareToShow(popup);
        popup.showUnderneathOf(this);
    }

    @NotNull
    protected ListPopup createPopupMenu() {
        return JBPopupFactory.getInstance().
                createActionGroupPopup(null, createActionGroup(), DataManager.getInstance().getDataContext(this),
                        JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, false);
    }

    protected Border createUnfocusedBorder() {
        return JBUI.Borders.empty(BORDER_SIZE);
    }

    private static Border wrapBorder(Border outerBorder) {
        return BorderFactory.createCompoundBorder(outerBorder, JBUI.Borders.empty(2));
    }

    private static final class DynamicLabel extends JLabel {
        private final Supplier<@NlsContexts.Label String> myText;

        private DynamicLabel(@NotNull Supplier<@NlsContexts.Label String> text) {myText = text;}

        @Override
        @NlsContexts.Label
        public String getText() {
            if (myText == null) return "";
            return myText.get();
        }
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleVcsLogPopupComponent(super.getAccessibleContext());
        }
        return accessibleContext;
    }

    private class AccessibleVcsLogPopupComponent extends AccessibleContextDelegate {

        AccessibleVcsLogPopupComponent(AccessibleContext context) {
            super(context);
        }

        @Override
        protected Container getDelegateParent() {
            return null;
        }

        @Override
        public String getAccessibleName() {
            return VcsLogBundle.message("vcs.log.Accessibility.filter.label", myNameLabel.getText(), myValueLabel.getText());
        }

        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.POPUP_MENU;
        }
    }

}
