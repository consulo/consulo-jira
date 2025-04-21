package com.intellij.jira.filter.priority;

import com.intellij.jira.filter.IssueFilterComponent;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DefaultActionGroup;
import consulo.ui.ex.action.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PriorityFilterComponent extends IssueFilterComponent<PriorityFilter, PriorityFilterModel> {

    public PriorityFilterComponent(@NotNull PriorityFilterModel filterModel) {
        super("Priority", filterModel);
    }

    @Override
    protected String getText(@NotNull PriorityFilter filter) {
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
        @NotNull protected final String myType;

        public IssuePriorityAction(@NotNull String value) {
            getTemplatePresentation().setText(value);
            myType = value;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            myFilterModel.setFilter(new PriorityFilterImpl(List.of(myType)));
        }
    }
}
