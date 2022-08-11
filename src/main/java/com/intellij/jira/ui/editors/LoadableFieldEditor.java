package com.intellij.jira.ui.editors;

import com.intellij.util.ui.AsyncProcessIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class LoadableFieldEditor<T> extends AbstractFieldEditor<T> {

    protected final AsyncProcessIcon myLoadingIcon = new AsyncProcessIcon("Getting possible values");

    @Nullable
    private volatile DataProvider<T> myDataProvider;
    @Nullable
    private volatile UpdateStatus myUpdateStatus;

    public LoadableFieldEditor(String fieldName, boolean required) {
        super(fieldName, null, required);
    }

    public final void setDataProvider(@NotNull DataProvider<T> dataProvider) {
        myDataProvider = dataProvider;

        Set<T> cachedValues = dataProvider.getCachedValues();
        if (cachedValues != null) {
            onUpdateValues(cachedValues);
        }
    }

    protected abstract void doUpdateValues(@NotNull Set<T> values);

    public final void onUpdateValues(@NotNull Set<T> values) {
        changeUpdateStatus(UpdateStatus.LOADED);
        doUpdateValues(values);
    }

    public final void reloadValuesInBackground() {
        changeUpdateStatus(UpdateStatus.LOADING);
        DataProvider<T> provider = myDataProvider;
        assert provider != null;
        provider.updateValuesAsynchronously();
    }

    private void changeUpdateStatus(@NotNull UpdateStatus status) {
        if (status == UpdateStatus.LOADING) {
            myLoadingIcon.resume();
        } else {
            myLoadingIcon.suspend();
            myLoadingIcon.setVisible(false);
        }

        myUpdateStatus = status;
    }

    protected void loaded() {
        changeUpdateStatus(UpdateStatus.LOADED);
    }

    public interface DataProvider<T> {

        /**
         * returns init cached values
         */
        @Nullable
        Set<T> getCachedValues();

        /**
         * After getting values method must call #onUpdateValues or #onValuesUpdateError
         */
        void updateValuesAsynchronously();
    }

    private enum UpdateStatus {
        LOADING, LOADED
    }
}
