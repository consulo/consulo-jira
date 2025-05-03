package com.intellij.jira.data;

import consulo.application.progress.ProgressIndicator;
import consulo.disposer.Disposable;
import jakarta.annotation.Nonnull;

public interface JiraProgress {

    void addProgressListener(JiraProgressListener listener, @Nonnull Disposable parent);

    void removeProgressListener(JiraProgressListener listener);

    ProgressIndicator createProgressIndicator();

    interface JiraProgressListener {

        void start();

        void finish();
    }
}
