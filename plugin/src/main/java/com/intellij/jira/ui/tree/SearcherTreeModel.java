package com.intellij.jira.ui.tree;

import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import consulo.project.Project;

import javax.swing.tree.DefaultTreeModel;
import java.util.List;

public class SearcherTreeModel extends DefaultTreeModel {

    private final Project myProject;
    private final SearcherTreeNode myRoot;


    public SearcherTreeModel(Project project) {
        this(project, SearcherTreeNode.newRootNode());
    }

    private SearcherTreeModel(Project project, SearcherTreeNode root) {
        super(root);

        myProject = project;
        myRoot = root;

        SearcherTreeNode globalNode = SearcherTreeNode.newGlobalNode();
        SearcherTreeNode projectNode = SearcherTreeNode.newProjectNode();

        List<JQLSearcher> searchers = JQLSearcherManager.getInstance().getSearcherList(project);
        searchers.forEach(searcher -> {
            if (searcher.isShared()) {
                boolean editable = !searcher.getAlias().equals("Assigned to me");
                globalNode.add(SearcherTreeNode.newLeafNode(searcher, editable));
            } else {
                projectNode.add(SearcherTreeNode.newLeafNode(searcher, true));
            }
        });

        myRoot.add(globalNode);
        myRoot.add(projectNode);
    }

    public void rebuild() {
        myRoot.removeAllChildren();
        SearcherTreeNode globalNode = SearcherTreeNode.newGlobalNode();
        SearcherTreeNode projectNode = SearcherTreeNode.newProjectNode();

        List<JQLSearcher> searchers = JQLSearcherManager.getInstance().getSearcherList(myProject);
        searchers.forEach(searcher -> {
            if (searcher.isShared()) {
                boolean editable = !searcher.getAlias().equals("Assigned to me");
                globalNode.add(SearcherTreeNode.newLeafNode(searcher, editable));
            } else {
                projectNode.add(SearcherTreeNode.newLeafNode(searcher, true));
            }
        });

        myRoot.add(globalNode);
        myRoot.add(projectNode);
        reload();
    }

}
