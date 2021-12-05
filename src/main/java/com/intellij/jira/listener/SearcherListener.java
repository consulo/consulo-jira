package com.intellij.jira.listener;

import com.intellij.jira.rest.model.jql.JQLSearcher;

public interface SearcherListener {

    void onAdded(JQLSearcher editedSearcher);

    void onChange(JQLSearcher editedSearcher);

    void onRemoved(JQLSearcher removedSearcher);

}
