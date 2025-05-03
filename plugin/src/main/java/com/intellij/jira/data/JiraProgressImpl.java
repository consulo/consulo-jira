package com.intellij.jira.data;

import consulo.application.ApplicationManager;
import consulo.application.progress.EmptyProgressIndicator;
import consulo.application.progress.ProgressIndicator;
import consulo.disposer.Disposable;
import consulo.disposer.Disposer;
import jakarta.annotation.Nonnull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JiraProgressImpl implements JiraProgress, Disposable {

    @Nonnull
    private final Object myLock = new Object();
    @Nonnull
    private final List<JiraProgressListener> myListeners = new ArrayList<>();
    @Nonnull
    private final Set<ProgressIndicator> myTasksWithVisibleProgress = new HashSet<>();

    public JiraProgressImpl(@Nonnull Disposable parent) {
        Disposer.register(parent, this);
    }

    @Override
    public void addProgressListener(JiraProgressListener listener, @Nonnull Disposable parent) {
        synchronized (myLock) {
            myListeners.add(listener);
            if (parent != null) {
                Disposer.register(parent, () -> removeProgressListener(listener));
            }
           // if (isRunning()) {
            //    Set<ProgressKey> keys = getRunningKeys();
               // ApplicationManager.getApplication().invokeLater(() -> listener.start());
           // }
        }
    }

    @Override
    public void removeProgressListener(JiraProgressListener listener) {
        synchronized (myLock) {
            myListeners.remove(listener);
        }
    }

    @Override
    public ProgressIndicator createProgressIndicator() {
        return new JiraProgressIndicator(true);
    }

    @Override
    public void dispose() {
        synchronized (myLock) {
            for (ProgressIndicator indicator : myTasksWithVisibleProgress) {
                indicator.cancel();
            }
        }
    }

    private void started(JiraProgressIndicator indicator) {
        synchronized (myLock) {
            List<JiraProgress.JiraProgressListener> list = new ArrayList<>(myListeners);
            ApplicationManager.getApplication().invokeLater(() -> list.forEach(JiraProgressListener::start));
        }
    }

    private void stopped(JiraProgressIndicator indicator) {
        synchronized (myLock) {
            List<JiraProgress.JiraProgressListener> list = new ArrayList<>(myListeners);
            ApplicationManager.getApplication().invokeLater(() -> list.forEach(JiraProgressListener::finish));
        }
    }

    private final class JiraProgressIndicator extends EmptyProgressIndicator {
       // @NotNull private ProgressKey myKey;
        private final boolean myVisible;

        private JiraProgressIndicator(boolean visible) {
            myVisible = visible;
        }

        @Override
        public void start() {
            super.start();
            started(this);
        }

        @Override
        public void stop() {
            super.stop();
            stopped(this);
        }



      /*  public void updateKey(@NotNull ProgressKey key) {
            synchronized (myLock) {
                Set<ProgressKey> oldKeys = getRunningKeys();
                myKey = key;
                keysUpdated(oldKeys);
            }
        }
*/
        public boolean isVisible() {
            return myVisible;
        }

       /* @NotNull
        public ProgressKey getKey() {
            synchronized (myLock) {
                return myKey;
            }
        }*/
    }

}
