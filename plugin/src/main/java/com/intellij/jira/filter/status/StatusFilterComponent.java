package com.intellij.jira.filter.status;

import com.intellij.jira.filter.IssueFilterComponent;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DefaultActionGroup;
import consulo.ui.ex.action.DumbAwareAction;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StatusFilterComponent extends IssueFilterComponent<StatusFilter, StatusFilterModel> {


    public StatusFilterComponent(@Nonnull StatusFilterModel filterModel) {
        super("Status", filterModel);
    }

    @Override
    protected String getText(@Nonnull StatusFilter issueStatusFilter) {
        return issueStatusFilter.getDisplayText();
    }

    @Override
    protected ActionGroup createActionGroup() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        actionGroup.add(createAllAction());

        Set<String> issueStatus = myFilterModel.getIssues().get().stream().map(issue -> issue.getStatus().getName()).collect(Collectors.toSet());

        issueStatus.forEach(status -> actionGroup.add(new IssueStatusAction(status)));

        return actionGroup;
    }

    protected class IssueStatusAction extends DumbAwareAction {
        @Nonnull
        protected final String myStatus;


        public IssueStatusAction(@Nonnull String value) {
            getTemplatePresentation().setText(value);
            myStatus = value;

        }

        @Override
        public void actionPerformed(@Nonnull AnActionEvent e) {
            myFilterModel.setFilter(new StatusFilterImpl(List.of(myStatus)));
        }
    }
}
