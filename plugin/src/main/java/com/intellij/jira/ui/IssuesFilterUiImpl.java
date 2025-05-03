package com.intellij.jira.ui;

import com.intellij.jira.data.Issues;
import com.intellij.jira.filter.FilterModel;
import com.intellij.jira.filter.IssueFilterCollection;
import com.intellij.jira.filter.IssueFilterCollectionImpl;
import com.intellij.jira.filter.assignee.AssigneeFilterComponent;
import com.intellij.jira.filter.assignee.AssigneeFilterModel;
import com.intellij.jira.filter.priority.PriorityFilterComponent;
import com.intellij.jira.filter.priority.PriorityFilterModel;
import com.intellij.jira.filter.status.StatusFilterComponent;
import com.intellij.jira.filter.status.StatusFilterModel;
import com.intellij.jira.filter.type.TypeFilterComponent;
import com.intellij.jira.filter.type.TypeFilterModel;
import consulo.proxy.EventDispatcher;
import consulo.ui.ex.action.*;
import consulo.ui.ex.awt.action.CustomComponentAction;
import jakarta.annotation.Nonnull;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IssuesFilterUiImpl implements IssuesFilterUi {

    private Issues myIssues;

    private final TypeFilterModel myIssueTypeFilterModel;
    private final StatusFilterModel myStatusFilterModel;
    private final PriorityFilterModel myPriorityFilterModel;
    private final AssigneeFilterModel myAssigneeFilterModel;

    private final EventDispatcher<IssueFilterListener> myFilterListenerDispatcher = EventDispatcher.create(IssueFilterListener.class);

    public IssuesFilterUiImpl(Consumer<IssueFilterCollection> filterConsumer, IssueFilterCollection filters) {
        myIssues = Issues.EMPTY;

        Supplier<Issues> issuesGetter = () -> myIssues;
        myIssueTypeFilterModel = new TypeFilterModel(issuesGetter, filters);
        myStatusFilterModel = new StatusFilterModel(issuesGetter, filters);
        myPriorityFilterModel = new PriorityFilterModel(issuesGetter, filters);
        myAssigneeFilterModel = new AssigneeFilterModel(issuesGetter, filters);

        FilterModel[] models = {myIssueTypeFilterModel, myPriorityFilterModel, myAssigneeFilterModel, myStatusFilterModel};
        for (FilterModel model : models) {
            model.addSetFilterListener(() -> {
                filterConsumer.accept(getFilters());
                myFilterListenerDispatcher.getMulticaster().onFiltersChanged();
            });
        }

    }

    @Override
    public IssueFilterCollection getFilters() {
        return new IssueFilterCollectionImpl(List.of(myIssueTypeFilterModel.getIssueTypeFilter(),
            myPriorityFilterModel.getPriorityFilter(),
            myAssigneeFilterModel.getAssigneeFilter(),
            myStatusFilterModel.getIssueTypeFilter()));
    }

    @Nonnull
    @Override
    public ActionGroup createActionGroup() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        FilterActionComponent issueTypeComponent = createIssueTypeComponent();
        actionGroup.add(issueTypeComponent);

        FilterActionComponent priorityComponent = createPriorityComponent();
        actionGroup.add(priorityComponent);

        FilterActionComponent assgineeComponent = createAssgineeComponent();
        actionGroup.add(assgineeComponent);

        FilterActionComponent issueStatusComponent = createIssueStatusComponent();
        actionGroup.add(issueStatusComponent);

        return actionGroup;
    }

    @Override
    public void updateIssues(Issues issues) {
        myIssues = issues;
    }

    @Override
    public void addFilterListener(@Nonnull IssueFilterListener listener) {
        myFilterListenerDispatcher.addListener(listener);
    }

    protected FilterActionComponent createIssueTypeComponent() {
        return new FilterActionComponent("Filter by Issue Type",
            () -> new TypeFilterComponent(myIssueTypeFilterModel).initUi());
    }

    private FilterActionComponent createPriorityComponent() {
        return new FilterActionComponent("Filter by Issue Priority",
            () -> new PriorityFilterComponent(myPriorityFilterModel).initUi());
    }

    private FilterActionComponent createAssgineeComponent() {
        return new FilterActionComponent("Filter by Issue Priority",
            () -> new AssigneeFilterComponent(myAssigneeFilterModel).initUi());
    }

    private FilterActionComponent createIssueStatusComponent() {
        return new FilterActionComponent("Filter by Status",
            () -> new StatusFilterComponent(myStatusFilterModel).initUi());
    }

    protected static class FilterActionComponent extends DumbAwareAction implements CustomComponentAction {

        @Nonnull
        private final Supplier<? extends JComponent> myComponentCreator;

        public FilterActionComponent(@Nonnull String dynamicText,
                                     @Nonnull Supplier<? extends JComponent> componentCreator) {
            super(dynamicText);
            myComponentCreator = componentCreator;
        }

        @Nonnull
        @Override
        public JComponent createCustomComponent(@Nonnull Presentation presentation, @Nonnull String place) {
            return myComponentCreator.get();
        }

        @Override
        public void actionPerformed(@Nonnull AnActionEvent e) {
            // TODO !!
//            MainVcsLogUi vcsLogUi = e.getData(VcsLogInternalDataKeys.MAIN_UI);
//            if (vcsLogUi == null) {
//                return;
//            }
//
//            Component actionComponent = UIUtil.uiTraverser(vcsLogUi.getToolbar()).traverse().find(component ->
//                ClientProperty.get(component, ACTION_KEY) == this
//            );
//
//            if (actionComponent instanceof IssueFilterComponent) {
//                ((IssueFilterComponent) actionComponent).showPopupMenu();
//            }
        }
    }
}
