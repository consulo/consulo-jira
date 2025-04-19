package com.intellij.jira.actions;

import com.intellij.jira.tasks.ChangeIssuePriorityTask;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;

import static java.util.Objects.isNull;

public class JiraIssueChangePriorityAction extends JiraIssueAction {

    private String priorityName;
    private String issueIdOrKey;


    public JiraIssueChangePriorityAction(String priorityName, String issueIdOrKey) {
        super(ActionProperties.of(priorityName));
        this.priorityName = priorityName;
        this.issueIdOrKey = issueIdOrKey;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(Project.KEY);
        if(isNull(project)){
            return;
        }

        new ChangeIssuePriorityTask(project, priorityName, issueIdOrKey).queue();
    }

}
