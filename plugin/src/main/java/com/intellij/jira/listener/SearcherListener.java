package com.intellij.jira.listener;

import com.intellij.jira.rest.model.jql.JQLSearcher;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.TopicAPI;

@TopicAPI(ComponentScope.APPLICATION)
public interface SearcherListener {

    void onAdded(JQLSearcher editedSearcher);

    void onChange(JQLSearcher editedSearcher);

    void onRemoved(JQLSearcher removedSearcher);

}
