// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package consulo.jira.impl;

import consulo.application.ApplicationManager;
import consulo.application.progress.ProgressIndicator;
import consulo.disposer.Disposable;
import consulo.disposer.Disposer;
import consulo.logging.Logger;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.NonNls;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Collects incoming requests into a list, and provides them to the underlying background task via {@link #popRequests()}. <br/>
 * Such task is started immediately after the first request arrives, if no other task is currently running. <br/>
 * A task reports its completion by calling {@link #taskCompleted(Object)} and providing a result which is immediately passed to the
 * result handler (unless it is null in which case the task is stopped but the result is not passed to the handler).
 * <p/>
 * The purpose of this class is to provide a single thread, which processes incoming requests in the background and continues to process
 * new ones if they arrive while the previous ones were processed. An alternative would be a long living thread which always checks some
 * queue for new requests - but current approach starts a thread only when needed, and finishes it once all requests are processed.
 * <p/>
 * The class is thread-safe: all operations are synchronized.
 */
public abstract class SingleTaskController<Request, Result> implements Disposable {
    protected static final Logger LOG = Logger.getInstance(SingleTaskController.class);

    @Nonnull
    private final String myName;
    private final @Nonnull Consumer<? super Result> myResultHandler;
    private final @Nonnull Object LOCK = new Object();

    private @Nonnull List<Request> myAwaitingRequests;
    private @Nullable SingleTask myRunningTask;

    private boolean myIsClosed = false;

    public SingleTaskController(@Nonnull @NonNls String name, @Nonnull Disposable parent, @Nonnull Consumer<? super Result> handler) {
        myName = name;
        myResultHandler = handler;
        myAwaitingRequests = new LinkedList<>();

        Disposer.register(parent, this);
    }


    /**
     * Posts requests into a queue. <br/>
     * If there is no active task, starts a new one. <br/>
     * Otherwise just remembers requests in the queue. Later they can be retrieved by {@link #popRequests()}.
     */
    public final void request(@Nonnull Request ... requests) {
        request(Arrays.asList(requests));
    }

    public void request(@Nonnull List<Request> requestList) {
        synchronized (LOCK) {
            if (myIsClosed) return;
            myAwaitingRequests.addAll(requestList);
            debug("Added requests: " + requestList);
            if (myRunningTask != null && cancelRunningTasks(requestList)) {
                cancelTask(myRunningTask);
            }
            if (myRunningTask == null) {
                myRunningTask = startNewBackgroundTask();
                debug("Started a new bg task " + myRunningTask);
            }
        }
    }

    protected boolean cancelRunningTasks(@Nonnull List<Request> requests) {
        return false;
    }

    private void debug(@Nonnull String message) {
        LOG.debug(formMessage(message));
    }

    private @Nonnull String formMessage(@Nonnull String message) {
        return "[" + myName + "] " + message;
    }

    private void cancelTask(@Nonnull SingleTask t) {
        if (t.isRunning()) {
            t.cancel();
            debug("Canceled task " + myRunningTask);
        }
    }

    /**
     * Starts new task on a background thread. <br/>
     * <b>NB:</b> Don't invoke StateController methods inside this method, otherwise a deadlock will happen.
     */
    protected abstract @Nonnull SingleTask startNewBackgroundTask();

    /**
     * Returns all awaiting requests and clears the queue. <br/>
     * I.e. the second call to this method will return an empty list (unless new requests came via {@link #request(Object[])}).
     */
    public final @Nonnull List<Request> popRequests() {
        synchronized (LOCK) {
            List<Request> requests = myAwaitingRequests;
            myAwaitingRequests = new LinkedList<>();
            debug("Popped requests: " + requests);
            return requests;
        }
    }

    public final @Nonnull List<Request> peekRequests() {
        synchronized (LOCK) {
            List<Request> requests = new ArrayList<>(myAwaitingRequests);
            debug("Peeked requests: " + requests);
            return requests;
        }
    }

    public final void removeRequests(@Nonnull List<Request> requests) {
        synchronized (LOCK) {
            myAwaitingRequests.removeAll(requests);
            debug("Removed requests: " + requests);
        }
    }

    public final @Nullable Request popRequest() {
        synchronized (LOCK) {
            if (myAwaitingRequests.isEmpty()) return null;
            Request request = myAwaitingRequests.remove(0);
            debug("Popped request: " + request);
            return request;
        }
    }

    /**
     * The underlying currently active task should use this method to inform that it has completed the execution. <br/>
     * If the result is not null, it is immediately passed to the result handler specified in the constructor.
     * Otherwise, result handler is not called, the task just completes.
     * After result handler is called, a new task is started if there are new requests awaiting in the queue.
     */
    public final void taskCompleted(@Nullable Result result) {
        if (result != null) {
            myResultHandler.accept(result);
            debug("Handled result: " + result);
        }
        synchronized (LOCK) {
            if (myAwaitingRequests.isEmpty()) {
                myRunningTask = null;
                debug("No more requests");
            }
            else {
                myRunningTask = startNewBackgroundTask();
                debug("Restarted a bg task " + myRunningTask);
            }
        }
    }

    public void cancelCurrentTask() {
        synchronized (LOCK) {
            if (myRunningTask != null) {
                myRunningTask.cancel();
            }
        }
    }

    private void closeQueue() {
        synchronized (LOCK) {
            if (myIsClosed) return;
            myIsClosed = true;

            if (myRunningTask != null) {
                myRunningTask.cancel();
            }

            myAwaitingRequests.clear();
        }
    }

    public boolean isClosed() {
        synchronized (LOCK) {
            return myIsClosed;
        }
    }

    @Override
    public void dispose() {
        SingleTask task = null;

        synchronized (LOCK) {
            closeQueue();

            if (myRunningTask != null) {
                task = myRunningTask;
                myRunningTask = null;
            }
        }

        if (task != null) {
            boolean longTimeOut = !ApplicationManager.getApplication().isDispatchThread() ||
                ApplicationManager.getApplication().isUnitTestMode();
            try {
                int timeout = longTimeOut ? 1000 : 20;
                task.waitFor(timeout, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException | ExecutionException e) {
                if (!(e.getCause() instanceof CancellationException)) {
                    LOG.debug(e);
                }
            }
            catch (CancellationException ignored) {
            }
            catch (TimeoutException e) {
                if (longTimeOut) LOG.warn(formMessage("Wait time out "), e);
            }
        }
    }

    public interface SingleTask {
        void waitFor(long timeout, @Nonnull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

        void cancel();

        boolean isRunning();
    }

    public static class SingleTaskImpl implements SingleTask {
        // TODO  private final @NotNull Future<?> myFuture;
        private final @Nonnull ProgressIndicator myIndicator;

        public SingleTaskImpl(/*@NotNull Future<?> future, */@Nonnull ProgressIndicator indicator) {
            // TODO  myFuture = future;
            myIndicator = indicator;
        }

        @Override
        public void waitFor(long timeout, @Nonnull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            // TODO myFuture.get(timeout, unit);
            cancel();
        }

        @Override
        public void cancel() {
            myIndicator.cancel();
        }

        @Override
        public boolean isRunning() {
            return myIndicator.isRunning();
        }
    }
}
