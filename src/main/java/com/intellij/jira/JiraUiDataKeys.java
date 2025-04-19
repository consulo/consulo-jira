package com.intellij.jira;

import com.intellij.jira.ui.AbstractIssuesUi;
import com.intellij.jira.ui.JiraIssueUiProperties;
import com.intellij.jira.ui.SearcherIssuesUi;
import com.intellij.jira.ui.panels.JiraIssuesPanel;
import com.intellij.jira.ui.tree.SearcherTreeNode;
import consulo.util.dataholder.Key;

public class JiraUiDataKeys {

    private JiraUiDataKeys() { }

    public static final Key<JiraIssueUiProperties> JIRA_UI_PROPERTIES = Key.create("jira.ui.properties");
    public static final Key<JiraIssuesPanel> ISSUES_PANEL = Key.create("issues.panel");
    public static final Key<AbstractIssuesUi> ISSUES_UI = Key.create("issues.ui");
    public static final Key<SearcherIssuesUi> SEARCHER_ISSUES_UI = Key.create("searcher.issues.ui");
    public static final Key<SearcherTreeNode> SEARCHER_TREE_NODE = Key.create("searcher.tree.node");

}
