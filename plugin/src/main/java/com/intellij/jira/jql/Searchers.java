package com.intellij.jira.jql;

import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.util.DividedListImpl;

import java.util.List;


public class Searchers extends DividedListImpl<JQLSearcher> {

    public Searchers(List<JQLSearcher> globalItems, List<JQLSearcher> projectItems) {
        super(globalItems, projectItems);
    }


}
