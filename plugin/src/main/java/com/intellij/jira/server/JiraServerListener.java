package com.intellij.jira.server;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.TopicAPI;

@TopicAPI(ComponentScope.APPLICATION)
public interface JiraServerListener {

    void onChangeSelectedServer();

}
