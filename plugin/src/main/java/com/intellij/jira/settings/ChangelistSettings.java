package com.intellij.jira.settings;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.application.ApplicationManager;
import consulo.component.persist.PersistentStateComponent;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.inject.Singleton;

@State(
        name = "ChangelistSettings",
        storages = @Storage("changelist.xml")
)
@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
@Singleton
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
    public void loadState(@Nonnull ChangelistState state) {
        this.myState = state;
    }

    public boolean isDefault() {
        return !isCustom();
    }

    public boolean isCustom() {
        return this.myState.isCustom();
    }
}
