package com.intellij.jira.jql;

import com.intellij.jira.listener.SearcherListener;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import consulo.application.ApplicationManager;
import consulo.project.Project;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.jira.jql.JQLSearcherApplicationManager.DEFAULT_JQL;
import static java.util.Objects.isNull;

public class JQLSearcherManager {

    public static final Class<SearcherListener> JQL_SEARCHERS_CHANGE = SearcherListener.class;

    public static JQLSearcherManager getInstance(){
        return ApplicationManager.getApplication().getInstance(JQLSearcherManager.class);
    }

    public JQLSearcher getSelectedSearcher(Project project){
        Searchers searchers = getSearchers(project);
        JQLSearcher selected = searchers.getSelected();
        return selected != null ? selected : DEFAULT_JQL;
    }

    public void add(Project project, JQLSearcher searcher){
        Searchers searchers = getSearchers(project);
        searchers.add(searcher, JQLSearcher::isShared);

        updateApplicationSearchers(searchers);
        updateProjectSearchers(project, searchers);

        ApplicationManager.getApplication().getMessageBus().syncPublisher(JQL_SEARCHERS_CHANGE).onAdded(searcher);
    }

    public void setSelected(Project project, JQLSearcher searcher) {
        Searchers searchers = getSearchers(project);
        searchers.setSelected(searcher);

        updateApplicationSearchers(searchers);
        updateProjectSearchers(project, searchers);

        ApplicationManager.getApplication().getMessageBus().syncPublisher(JQL_SEARCHERS_CHANGE).onChange(searcher);
    }

    public void update(Project project, String searcherId, JQLSearcher updatedSearcher){
        JQLSearcher oldSearcher = findById(project, searcherId);
        if(isNull(oldSearcher)){
            return;
        }

        Searchers searchers = getSearchers(project);
        searchers.update(updatedSearcher);

        updateApplicationSearchers(searchers);
        updateProjectSearchers(project, searchers);


        ApplicationManager.getApplication().getMessageBus().syncPublisher(JQL_SEARCHERS_CHANGE).onChange(updatedSearcher);
    }

    public void remove(Project project, String searcherId, JQLSearcher searcher) {
        JQLSearcher searcherToRemove = findById(project, searcherId);
        if(isNull(searcherToRemove)){
            return;
        }

        Searchers searchers = getSearchers(project);
        searchers.remove(searcherToRemove);

        updateApplicationSearchers(searchers);
        updateProjectSearchers(project, searchers);

        ApplicationManager.getApplication().getMessageBus().syncPublisher(JQL_SEARCHERS_CHANGE).onRemoved(searcher);

    }

    public void moveToGlobal(Project project, JQLSearcher searcher) {
        Searchers searchers = getSearchers(project);
        searchers.moveToFirst(searcher);

        updateApplicationSearchers(searchers);
        updateProjectSearchers(project, searchers);

        ApplicationManager.getApplication().getMessageBus().syncPublisher(JQL_SEARCHERS_CHANGE).onChange(searcher);
    }

    public void moveToProject(Project project, JQLSearcher searcher) {
        Searchers searchers = getSearchers(project);
        searchers.moveToSecond(searcher);

        updateApplicationSearchers(searchers);
        updateProjectSearchers(project, searchers);

        ApplicationManager.getApplication().getMessageBus().syncPublisher(JQL_SEARCHERS_CHANGE).onChange(searcher);
    }

    @Nullable
    public JQLSearcher findByAlias(Project project, String alias){
        return getSearchers(project).getAll().stream()
                .filter(searcher -> searcher.getAlias().equalsIgnoreCase(alias))
                .findFirst().orElse(null);
    }

    @Nullable
    public JQLSearcher findById(Project project, String id){
        return getSearchers(project).getAll().stream()
                .filter(searcher -> searcher.getId().equals(id))
                .findFirst().orElse(null);
    }

    private void updateProjectSearchers(Project project, Searchers searchers){
        getJqlSearcherProjectManager(project).setSearchers(searchers.getSecondList(), searchers.getSelectedIndex());
    }

    private void updateApplicationSearchers(Searchers searchers){
        getJqlSearcherApplicationManager().setSearchers(searchers.getFirstList());
    }

    private JQLSearcherApplicationManager getJqlSearcherApplicationManager(){
        return JQLSearcherApplicationManager.getInstance();
    }

    private JQLSearcherProjectManager getJqlSearcherProjectManager(Project project){
        return JQLSearcherProjectManager.getInstance(project);
    }

    public Searchers getSearchers(Project project){
        JQLSearcherProjectManager searcherProjectManager = getJqlSearcherProjectManager(project);
        List<JQLSearcher> globalSearchers = getJqlSearcherApplicationManager().getSearchers();
        List<JQLSearcher> projectSearchers = searcherProjectManager.getSearchers();

        Searchers searchers = new Searchers(globalSearchers, projectSearchers);
        searchers.setSelected(searcherProjectManager.getSelectedSearcherIndex());

        return searchers;
    }

    public List<JQLSearcher> getSearcherList(Project project) {
        return getSearchers(project).getAll();
    }

    public boolean isSelected(Project project, JQLSearcher searcher) {
        return getSelectedSearcher(project).getId().equals(searcher.getId());
    }

}
