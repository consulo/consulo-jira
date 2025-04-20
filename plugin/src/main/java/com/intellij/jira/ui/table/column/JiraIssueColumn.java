package com.intellij.jira.ui.table.column;

import consulo.ui.ex.awt.ColumnInfo;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class JiraIssueColumn<T, V> extends ColumnInfo<T, V> {

    private String id;
    private boolean hideable;
    private boolean resizable;

    protected JiraIssueColumn(String id, String name) {
        this (id, name, false);
    }

    protected JiraIssueColumn(String id, String name, boolean resizable) {
        this (id, name, resizable, false);
    }

    protected JiraIssueColumn(String id, String name, boolean resizable, boolean hideable) {
        super(name);
        this.id = id;
        this.resizable = resizable;
        this.hideable = hideable;
    }

    public String getId() {
        return id;
    }

    public boolean isHideable() {
        return hideable;
    }

    public boolean isResizable() {
        return resizable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JiraIssueColumn<?, ?> that = (JiraIssueColumn<?, ?>) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public @Nullable String getMaxStringValue() {
        return JiraIssueColumnManager.getInstance().getColumnWidth(getId());
    }
}
