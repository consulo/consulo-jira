package com.intellij.jira.data;

import consulo.application.progress.ProgressIndicator;
import consulo.disposer.Disposable;
import org.jetbrains.annotations.NotNull;

public interface JiraProgress {

    void addProgressListener(JiraProgressListener listener, @NotNull Disposable parent);

    void removeProgressListener(JiraProgressListener listener);

    ProgressIndicator createProgressIndicator();

    interface JiraProgressListener {

        void start();

        void finish();
    }
}
