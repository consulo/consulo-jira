// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.tasks.jira;

import com.intellij.tasks.jira.model.api2.JiraRestApi2;
import com.intellij.tasks.jira.model.api20alpha1.JiraRestApi20Alpha1;
import com.intellij.tasks.jira.soap.JiraLegacyApi;
import consulo.task.CustomTaskState;
import consulo.task.LocalTask;
import consulo.task.Task;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Because of the number of available remote interfaces in JIRA, {@link JiraRepository} delegates
 * almost all its functionality to classes extending this class.
 *
 * @author Mikhail Golubev
 */
public abstract class JiraRemoteApi {
    protected final JiraRepository myRepository;

    protected JiraRemoteApi(@Nonnull JiraRepository repository) {
        myRepository = repository;
    }

    public abstract @Nonnull List<Task> findTasks(@Nonnull String jql, int max) throws Exception;

    public abstract @Nullable Task findTask(@Nonnull String key) throws Exception;

    public abstract @Nonnull Set<CustomTaskState> getAvailableTaskStates(@Nonnull Task task) throws Exception;

    public abstract void setTaskState(@Nonnull Task task, @Nonnull CustomTaskState state) throws Exception;

    public abstract void updateTimeSpend(@Nonnull LocalTask task, @Nonnull String timeSpent, String comment) throws Exception;

    public final @Nonnull String getVersionName() {
        return getType().getVersionName();
    }

    @Override
    public final String toString() {
        return "JiraRemoteApi(" + getType().getVersionName() + ")";
    }

    public abstract @Nonnull ApiType getType();

    public enum ApiType {
        LEGACY("XML-RPC + RSS") {
            @Override
            public @Nonnull JiraLegacyApi createApi(@Nonnull JiraRepository repository) {
                return new JiraLegacyApi(repository);
            }
        },
        REST_2_0("REST 2.0") {
            @Override
            public @Nonnull JiraRestApi2 createApi(@Nonnull JiraRepository repository) {
                return new JiraRestApi2(repository);
            }
        },
        REST_2_0_ALPHA("REST 2.0.alpha1") {
            @Override
            public @Nonnull JiraRestApi20Alpha1 createApi(@Nonnull JiraRepository repository) {
                return new JiraRestApi20Alpha1(repository);
            }
        };

        ApiType(String versionName) {
            myVersionName = versionName;
        }

        private final String myVersionName;

        public abstract @Nonnull JiraRemoteApi createApi(@Nonnull JiraRepository repository);

        public @Nonnull String getVersionName() {
            return myVersionName;
        }
    }
}
