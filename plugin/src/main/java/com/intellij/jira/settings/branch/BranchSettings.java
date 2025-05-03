package com.intellij.jira.settings.branch;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.application.ApplicationManager;
import consulo.component.persist.PersistentStateComponent;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import consulo.util.xml.serializer.XmlSerializerUtil;
import jakarta.inject.Singleton;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Arrays;

@State(
        name = "BranchSettings",
        storages = @Storage("branchSettings.xml")
)
@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
@Singleton
public class BranchSettings implements PersistentStateComponent<BranchSettingsState> {

    private BranchSettingsState myState = BranchSettingsState.getDefault(Arrays.asList("feature", "bugfix", "hotfix"));

    public static BranchSettings getInstance() {
        return ApplicationManager.getApplication().getInstance(BranchSettings.class);
    }

    @Override
    public @Nullable BranchSettingsState getState() {
        return myState;
    }

    @Override
    public void loadState(@Nonnull BranchSettingsState state) {
        XmlSerializerUtil.copyBean(state, myState);
    }
}
