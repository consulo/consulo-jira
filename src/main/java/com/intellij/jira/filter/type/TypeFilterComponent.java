package com.intellij.jira.filter.type;

import com.intellij.jira.filter.IssueFilterComponent;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DefaultActionGroup;
import consulo.ui.ex.action.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TypeFilterComponent extends IssueFilterComponent<TypeFilter, TypeFilterModel> {

    public TypeFilterComponent(TypeFilterModel typeFilterModel) {
        super(() -> "Type", typeFilterModel);
    }

    @Override
    protected String getText(@NotNull TypeFilter typeFilter) {
        return typeFilter.getDisplayText();
    }

    @Override
    protected ActionGroup createActionGroup() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        actionGroup.add(createAllAction());

        Set<String> issueTypes = myFilterModel.getIssues().get().stream().map(issue -> issue.getIssuetype().getName()).collect(Collectors.toSet());

        issueTypes.forEach(issueType -> actionGroup.add(new IssueTypeAction(issueType)));

        return actionGroup;
    }

    protected class IssueTypeAction extends DumbAwareAction {
        @NotNull protected final String myType;


        public IssueTypeAction(@NotNull String value) {
            getTemplatePresentation().setText(value);
            myType = value;

        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            myFilterModel.setFilter(new TypeFilterImpl(List.of(myType)));
        }
    }

}
