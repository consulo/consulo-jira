package com.intellij.jira.data;

import com.intellij.jira.rest.model.JiraIssue;
import consulo.disposer.Disposable;
import consulo.disposer.Disposer;
import consulo.project.Project;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;

public class JiraIssuesData implements Disposable {

    private final Project myProject;
    private final IssuesGetter myIssuesGetter;

    public JiraIssuesData(@Nonnull Project project, @Nonnull Disposable parent) {
        myProject = project;
        myIssuesGetter = new IssuesGetter(project);

        Disposer.register(parent, this);
    }

    public Project getProject() {
        return myProject;
    }


    public List<JiraIssue> getIssues(String jql) {
        return myIssuesGetter.getIssues(jql);
    }

    @Nullable
    public JiraIssue getIssue(String issueKey) {
        return myIssuesGetter.getIssue(issueKey);
    }

    @Override
    public void dispose() {

    }

    public interface IssuesChangeListener {
        void onIssuesChange(Issues newIssues);
    }
}
