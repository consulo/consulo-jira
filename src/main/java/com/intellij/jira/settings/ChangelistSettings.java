package com.intellij.jira.settings;

import consulo.application.ApplicationManager;
import consulo.component.persist.PersistentStateComponent;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "ChangelistSettings",
        storages = @Storage("changelist.xml")
)
public class ChangelistSettings implements PersistentStateComponent<ChangelistState> {

    private ChangelistState myState = ChangelistState.getDefault();

    public static ChangelistSettings getInstance() {
        return ApplicationManager.getApplication().getInstance(ChangelistSettings.class);
    }

    @Nullable
    @Override
    public ChangelistState getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull ChangelistState state) {
        this.myState = state;
    }

    public boolean isDefault() {
        return !isCustom();
    }

    public boolean isCustom() {
        return this.myState.isCustom();
    }
}
