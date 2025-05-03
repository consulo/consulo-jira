package com.intellij.jira.ui.tree;

import com.intellij.jira.rest.model.jql.JQLSearcher;
import jakarta.annotation.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;

public class SearcherTreeNode extends DefaultMutableTreeNode {

    private final SearcherNodeDescriptor myDescriptor;
    @Nullable
    private final JQLSearcher mySearcher;
    private final boolean myIsEditable;

    public SearcherTreeNode(SearcherNodeDescriptor descriptor) {
        this(descriptor, null, false);
    }

    public SearcherTreeNode(SearcherNodeDescriptor descriptor, @Nullable JQLSearcher searcher, boolean editable) {
        myDescriptor = descriptor;
        mySearcher = searcher;
        myIsEditable = editable;
    }

    public static SearcherTreeNode newRootNode() {
        return new SearcherTreeNode(new SearcherNodeDescriptor(NodeType.ROOT));
    }

    public static SearcherTreeNode newGlobalNode() {
        return new SearcherTreeNode(new SearcherNodeDescriptor(NodeType.GLOBAL));
    }

    public static SearcherTreeNode newProjectNode() {
        return new SearcherTreeNode(new SearcherNodeDescriptor(NodeType.PROJECT));
    }

    public static SearcherTreeNode newLeafNode(JQLSearcher searcher, boolean editable) {
        return new SearcherTreeNode(new SearcherNodeDescriptor(NodeType.LEAF), searcher, editable);
    }

    public boolean isEditable() {
        return myIsEditable;
    }

    public String getText() {
        switch (myDescriptor.getType()) {
            case GLOBAL: return "Global";
            case PROJECT: return "Project";
            case LEAF: return mySearcher.getAlias();
            default: return "Searchers";
        }
    }

    @Nullable
    public JQLSearcher getSearcher() {
        return mySearcher;
    }

    public enum NodeType {
        ROOT, GLOBAL, PROJECT, LEAF
    }

    public static class SearcherNodeDescriptor {

        private final NodeType myType;

        public SearcherNodeDescriptor(NodeType myType) {
            this.myType = myType;
        }

        public NodeType getType() {
            return myType;
        }
    }


}
