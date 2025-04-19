package com.intellij.jira.settings.branch;

import consulo.application.ApplicationManager;
import consulo.component.persist.PersistentStateComponent;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import consulo.util.xml.serializer.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@State(
        name = "BranchSettings",
        storages = @Storage("branchSettings.xml")
)
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
    public void loadState(@NotNull BranchSettingsState state) {
        XmlSerializerUtil.copyBean(state, myState);
    }
}
