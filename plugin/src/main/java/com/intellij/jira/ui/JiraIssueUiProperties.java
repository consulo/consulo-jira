package com.intellij.jira.ui;

import consulo.jira.impl.ui.ValueKey;
import consulo.ui.annotation.RequiredUIAccess;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.NonNls;

import java.util.EventListener;
import java.util.Objects;

public interface JiraIssueUiProperties {

    @Nonnull
    <T> T get(@Nonnull JiraIssueUiProperties.JiraIssueUiProperty<T> property);

    <T> void set(@Nonnull JiraIssueUiProperties.JiraIssueUiProperty<T> property, @Nonnull T value);

    <T> boolean exists(@Nonnull JiraIssueUiProperties.JiraIssueUiProperty<T> property);

    @RequiredUIAccess
    void addChangeListener(@Nonnull JiraIssueUiProperties.PropertyChangeListener listener);

    @RequiredUIAccess
    void removeChangeListener(@Nonnull JiraIssueUiProperties.PropertyChangeListener listener);

    class JiraIssueUiProperty<T> implements ValueKey<T> {
        @Nonnull
        private final String myName;

        public JiraIssueUiProperty(@NonNls @Nonnull String name) {
            myName = name;
        }

        @Nonnull
        @Override
        public String getName() {
            return myName;
        }

        @Override
        public String toString() {
            return myName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            JiraIssueUiProperties.JiraIssueUiProperty<?> property = (JiraIssueUiProperties.JiraIssueUiProperty<?>)o;
            return Objects.equals(myName, property.myName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(myName);
        }
    }

    interface PropertyChangeListener extends EventListener {
        <T> void onChanged(@Nonnull JiraIssueUiProperties.JiraIssueUiProperty<T> property);
    }

}
