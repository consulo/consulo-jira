package com.intellij.jira.ui;

import com.intellij.jira.data.Issues;
import com.intellij.jira.data.JiraIssuesData;
import com.intellij.jira.data.JiraIssuesRefresherImpl;
import com.intellij.jira.data.JiraProgress;
import com.intellij.jira.data.JiraProgressImpl;
import com.intellij.jira.listener.IssueCreatedListener;
import com.intellij.jira.data.JiraVisibleIssuesRefresher;
import com.intellij.jira.data.JiraVisibleIssuesRefresherImpl;
import com.intellij.jira.filter.IssueFilter;
import com.intellij.jira.filter.IssueFilterCollection;
import com.intellij.jira.filter.IssueFilterCollectionImpl;
import com.intellij.jira.filter.IssueFilterer;
import com.intellij.jira.filter.status.IssueStatusFilterImpl;
import com.intellij.jira.filter.type.TypeFilterImpl;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.highlighters.JiraIssueHighlighter;
import com.intellij.jira.ui.highlighters.JiraIssueHighlighterFactory;
import com.intellij.jira.ui.highlighters.JiraIssueHighlighterProperty;
import com.intellij.jira.ui.table.column.JiraIssueApplicationSettings;
import com.intellij.jira.ui.table.column.JiraIssueColumnProperties;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intellij.jira.actions.HighlightersActionGroup.JIRA_ISSUE_HIGHLIGHTER_FACTORY_EP;

public abstract class AbstractIssuesUi implements IssuesUi {

    private final String myId;
    protected final JiraIssuesData myIssuesData;
    protected final Map<String, JiraIssueHighlighter> myHighlighters = new HashMap<>();
    private final MyIssuesChangeListener myIssuesChangeListener;

    private final JiraIssuesRefresherImpl myRefresher;
    @NotNull private final List<JiraIssuesData.IssuesChangeListener> myIssuesChangeListeners = ContainerUtil.createLockFreeCopyOnWriteList();

    private JiraVisibleIssuesRefresher myVisibleIssuesRefresher;
    private JiraVisibleIssuesRefresher.VisibleIssueChangeListener myVisibleIssueChangeListener;

    private IssuesFilterUi myFilterUi;
    private Issues myIssues = Issues.EMPTY;

    protected AbstractIssuesUi(String id, JiraIssuesData issuesData) {
        myId = id;
        myIssuesData = issuesData;

        // Refresh tab issues
        JiraProgress progress = new JiraProgressImpl(this);
        myRefresher = new JiraIssuesRefresherImpl(issuesData.getProject(), progress, this::fireIssuesChangeEvent);

        Disposer.register(this, myRefresher);

        // Create filter UI
        List<IssueFilter> issueFilters = new ArrayList<>();
        issueFilters.add(new TypeFilterImpl());
        issueFilters.add(new IssueStatusFilterImpl());

        IssueFilterCollection initialFilters = new IssueFilterCollectionImpl(issueFilters);
        myFilterUi = new IssuesFilterUiImpl(filters -> applyFilters(filters), initialFilters);

        // Apply filters to issues
        myVisibleIssuesRefresher = new JiraVisibleIssuesRefresherImpl(issuesData, myIssues, progress, myFilterUi.getFilters(), new IssueFilterer());

        myVisibleIssueChangeListener = issues -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                setIssues(issues);
            });
        };

        myIssuesChangeListener = new MyIssuesChangeListener();
        addIssuesChangeListener(myIssuesChangeListener);

        issuesData.getProject().getMessageBus().connect().subscribe(IssueCreatedListener.TOPIC, createdIssue -> refresh());

        myVisibleIssuesRefresher.addVisibleIssueChangeListener(myVisibleIssueChangeListener);

        Disposer.register(this, myVisibleIssuesRefresher);

        ApplicationManager.getApplication().getService(JiraIssueApplicationSettings.class)
                .addChangeListener(new MyPropertyChangeListener());
    }

    public void addIssuesChangeListener(JiraIssuesData.IssuesChangeListener listener) {
        myIssuesChangeListeners.add(listener);
    }

    public void removeIssuesChangeListener(JiraIssuesData.IssuesChangeListener listener) {
        myIssuesChangeListeners.remove(listener);
    }


    private void fireIssuesChangeEvent(@NotNull final Issues issues) {
        ApplicationManager.getApplication().invokeLater(() -> {
            for (JiraIssuesData.IssuesChangeListener listener : myIssuesChangeListeners) {
                listener.onIssuesChange(issues);
            }
        }, o -> Disposer.isDisposed(this));
    }

    public JiraProgress getProgress() {
        return myRefresher.getProgress();
    }

    abstract void setIssues(Issues issues);

    @NotNull
    abstract JQLSearcher getSearcher();

    public JiraVisibleIssuesRefresher getRefresher() {
        return myVisibleIssuesRefresher;
    }

    public void refresh() {
       myRefresher.getIssues(getSearcher().getJql());
    }

    public void applyFilters(IssueFilterCollection filters) {
        myVisibleIssuesRefresher.onFiltersChange(filters);
    }

    @NotNull
    @Override
    public String getId() {
        return myId;
    }

    @Override
    public IssuesFilterUi getFilterUi() {
        return myFilterUi;
    }

    @NotNull
    @Override
    public Issues getIssues() {
        return myIssues;
    }

    @Override
    public void dispose() {
        myHighlighters.clear();
        removeIssuesChangeListener(myIssuesChangeListener);
        myVisibleIssuesRefresher.removeVisibleIssueChangeListener(myVisibleIssueChangeListener);
        myIssues = Issues.EMPTY;
    }

    protected void updateHighlighters() {
        myHighlighters.forEach((id, highlighter) -> getTable().removeHighlighter(highlighter));
        myHighlighters.clear();

        for (JiraIssueHighlighterFactory factory : JIRA_ISSUE_HIGHLIGHTER_FACTORY_EP.getExtensionList()) {
            JiraIssueApplicationSettings properties = ApplicationManager.getApplication().getService(JiraIssueApplicationSettings.class);
            JiraIssueHighlighterProperty highlighterProperty = JiraIssueHighlighterProperty.get(factory.getId());
            if (properties.get(highlighterProperty)) {
                JiraIssueHighlighter highlighter = factory.createHighlighter(myIssuesData);
                myHighlighters.put(factory.getId(), highlighter);
                getTable().addHighlighter(highlighter);
            }
        }

        getTable().repaint();
    }

    private class MyPropertyChangeListener implements JiraIssueUiProperties.PropertyChangeListener {

        @Override
        public <T> void onChanged(JiraIssueUiProperties.@NotNull JiraIssueUiProperty<T> property) {
            if (property instanceof JiraIssueHighlighterProperty) {
                updateHighlighters();
            } else if (property instanceof JiraIssueColumnProperties.TableColumnVisibilityProperty) {
                getTable().getModel().update();
                getTable().createDefaultColumnsFromModel();
                getTable().updateColumnSizes();
            }
        }
    }

    private class MyIssuesChangeListener implements JiraIssuesData.IssuesChangeListener {

        @Override
        public void onIssuesChange(Issues newIssues) {
            setIssues(newIssues);
            myFilterUi.updateIssues(newIssues);
            myVisibleIssuesRefresher.updateIssues(newIssues);
        }
    }

}
