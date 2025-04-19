package com.intellij.jira.actions;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.components.JiraNotificationManager;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.settings.branch.BranchSettings;
import com.intellij.jira.settings.branch.BranchSettingsState;
import com.intellij.jira.util.provider.ProviderFactory;
import com.intellij.jira.util.provider.ProviderFactoryImpl;
import consulo.application.AllIcons;
import consulo.application.ApplicationManager;
import consulo.project.Project;
import consulo.project.ui.notification.Notifications;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.AnSeparator;
import consulo.ui.ex.action.DefaultActionGroup;
import git4idea.branch.GitBranchUtil;
import git4idea.branch.GitBrancher;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static git4idea.GitUtil.HEAD;

public class BranchActionGroup extends DefaultActionGroup {


    public BranchActionGroup() {
        super("Branch", true);
        getTemplatePresentation().setIcon(AllIcons.Vcs.Branch);
    }


    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        List<AnAction> actions = new ArrayList<>();
        DefaultActionGroup newBranchActions = new DefaultActionGroup("New Branch...", true);
        newBranchActions.getTemplatePresentation().setIcon(AllIcons.General.Add);


        if (Objects.nonNull(e)) {
            JiraIssue issue = e.getData(JiraDataKeys.ISSUE);
            if (Objects.nonNull(issue)) {
                newBranchActions.add(NewBranchAction.withName(issue.getKey()));

                BranchSettings branchSettings = BranchSettings.getInstance();
                BranchSettingsState state = branchSettings.getState();

                if (Objects.nonNull(state)) {
                    String name = state.getFieldNames()
                            .stream()
                            .map(fieldName -> resolveFieldName(issue, fieldName))
                            .filter(Objects::nonNull)
                            .collect(Collectors.joining(state.getFieldSeparator().getSeparator()));

                    state.getBranchTypes()
                            .forEach(type -> newBranchActions.add(NewBranchAction.withName(type + "/" + name)));
                }

                actions.add(newBranchActions);
            }

            actions.add(new AnSeparator());
            actions.add(new BranchSettingsDialogAction());

        }

        return actions.toArray(AnAction.EMPTY_ARRAY);
    }


    private String resolveFieldName(JiraIssue issue, String fieldName) {
        ProviderFactory providerFactory = ApplicationManager.getApplication().getService(ProviderFactoryImpl.class);
        return providerFactory.get(fieldName).getValue(issue);
    }

    private static class NewBranchAction extends AnAction {

        private final String myBranchName;

        private NewBranchAction(@Nullable  String text) {
            super(text);
            myBranchName = text;
        }

        public static NewBranchAction withName(String name) {
            return new NewBranchAction(name);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Project project = e.getData(Project.KEY);
            if (Objects.isNull(project)) {
                return;
            }


            GitRepository gitRepository = GitBranchUtil.guessRepositoryForOperation(project, e.getDataContext());
            if (Objects.nonNull(gitRepository)) {
                GitBrancher.getInstance(project).createBranch(myBranchName, Map.of(gitRepository, HEAD));
            } else {
                Notifications.Bus.notify(JiraNotificationManager.getInstance().createNotificationError("Branch creation failed", "Repository not found"));
            }



        }


    }

}
