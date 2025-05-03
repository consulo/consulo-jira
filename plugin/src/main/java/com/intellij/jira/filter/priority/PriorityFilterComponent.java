package com.intellij.jira.filter.priority;

import com.intellij.jira.filter.IssueFilterComponent;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DefaultActionGroup;
import consulo.ui.ex.action.DumbAwareAction;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PriorityFilterComponent extends IssueFilterComponent<PriorityFilter, PriorityFilterModel> {

    public PriorityFilterComponent(@Nonnull PriorityFilterModel filterModel) {
        super("Priority", filterModel);
    }

    @Override
    protected String getText(@Nonnull PriorityFilter filter) {
        return filter.getDisplayText();
    }

    @Override
    protected ActionGroup createActionGroup() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(createAllAction());

        Set<String> issueTypes = myFilterModel.getIssues().get().stream().map(issue -> issue.getPriority().getName()).collect(Collectors.toSet());
        issueTypes.forEach(issueType -> actionGroup.add(new IssuePriorityAction(issueType)));

        return actionGroup;
    }

    protected class IssuePriorityAction extends DumbAwareAction {
        @Nonnull
        protected final String myType;

        public IssuePriorityAction(@Nonnull String value) {
            getTemplatePresentation().setText(value);
            myType = value;
        }

        @Override
        public void actionPerformed(@Nonnull AnActionEvent e) {
            myFilterModel.setFilter(new PriorityFilterImpl(List.of(myType)));
        }
    }
}
