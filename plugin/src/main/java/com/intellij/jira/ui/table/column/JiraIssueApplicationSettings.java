package com.intellij.jira.ui.table.column;

import com.intellij.jira.ui.JiraIssueUiProperties;
import com.intellij.jira.ui.SearcherIssuesUi;
import com.intellij.jira.ui.highlighters.JiraIssueHighlighterProperty;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.component.persist.PersistentStateComponent;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import consulo.proxy.EventDispatcher;
import jakarta.annotation.Nonnull;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


@State(name = "Jira.Issues.App.Settings", storages = @Storage("issues.xml"))
@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
@Singleton
public class JiraIssueApplicationSettings implements PersistentStateComponent<JiraIssueApplicationSettings.State>, JiraIssueUiProperties {

    private final EventDispatcher<PropertyChangeListener> myEventDispatcher = EventDispatcher.create(PropertyChangeListener.class);

    private State myState = new State();

    @Nonnull
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@Nonnull State state) {
        this.myState = state;
    }

    @Override
    @Nonnull
    public <T> T get(@Nonnull JiraIssueUiProperty<T> property) {
        if (property instanceof JiraIssueColumnProperties.TableColumnVisibilityProperty) {
            JiraIssueColumnProperties.TableColumnVisibilityProperty visibilityProperty = (JiraIssueColumnProperties.TableColumnVisibilityProperty) property;
            Boolean isVisible = myState.COLUMN_ID_VISIBILITY.get(visibilityProperty.getName());
            if (isVisible != null) {
                return (T) isVisible;
            }

            if (JiraIssueColumnManager.getInstance().getCurrentColumns().contains(visibilityProperty.getColumn())) {
                return (T) Boolean.TRUE;
            }

        }
        else if (property instanceof JiraIssueHighlighterProperty) {
            Boolean result = getState().HIGHLIGHTERS.get(((JiraIssueHighlighterProperty) property).getId());
            if (result == null) {
                return (T) Boolean.TRUE;
            }

            return (T) result;
        }
        else if (property instanceof SearcherIssuesUi.ShowSearchersProperty) {
            Boolean result = getState().CUSTOM_BOOLEAN_PROPERTIES.get(property.getName());
            if (result == null) {
                return (T) Boolean.FALSE;
            }

            return (T) result;
        }

        return (T) Boolean.FALSE;
    }

    @Override
    public <T> void set(@Nonnull JiraIssueUiProperty<T> property, @Nonnull T value) {
        if (property instanceof JiraIssueColumnProperties.TableColumnVisibilityProperty) {
            getState().COLUMN_ID_VISIBILITY.put(property.getName(), (Boolean) value);
        }
        else if (property instanceof JiraIssueHighlighterProperty) {
            getState().HIGHLIGHTERS.put(((JiraIssueHighlighterProperty) property).getId(), (Boolean) value);
        }
        else if (property instanceof SearcherIssuesUi.ShowSearchersProperty) {
            getState().CUSTOM_BOOLEAN_PROPERTIES.put(property.getName(), (Boolean) value);
        }

        myEventDispatcher.getMulticaster().onChanged(property);
    }

    @Override
    public <T> boolean exists(@Nonnull JiraIssueUiProperty<T> property) {
        return property instanceof JiraIssueColumnProperties.TableColumnVisibilityProperty
            || property instanceof JiraIssueHighlighterProperty
            || property instanceof SearcherIssuesUi.ShowSearchersProperty;
    }

    @Override
    public void addChangeListener(@Nonnull PropertyChangeListener listener) {
        myEventDispatcher.addListener(listener);
    }

    @Override
    public void removeChangeListener(@Nonnull PropertyChangeListener listener) {
        myEventDispatcher.removeListener(listener);
    }

    public static class State {
        public Map<String, Boolean> HIGHLIGHTERS = new TreeMap<>();
        public Map<String, Boolean> COLUMN_ID_VISIBILITY = new HashMap<>();
        public Map<String, Boolean> CUSTOM_BOOLEAN_PROPERTIES = new HashMap<>();
    }

}
