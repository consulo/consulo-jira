package com.intellij.jira.ui;

import consulo.ui.annotation.RequiredUIAccess;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.EventListener;
import java.util.Objects;

public interface JiraIssueUiProperties {

    @NotNull <T> T get(@NotNull JiraIssueUiProperties.JiraIssueUiProperty<T> property);

    <T> void set(@NotNull JiraIssueUiProperties.JiraIssueUiProperty<T> property, @NotNull T value);

    <T> boolean exists(@NotNull JiraIssueUiProperties.JiraIssueUiProperty<T> property);

    @RequiredUIAccess
    void addChangeListener(@NotNull JiraIssueUiProperties.PropertyChangeListener listener);

    @RequiredUIAccess
    void removeChangeListener(@NotNull JiraIssueUiProperties.PropertyChangeListener listener);

    class JiraIssueUiProperty<T> implements ValueKey<T> {
        @NotNull private final String myName;

        public JiraIssueUiProperty(@NonNls @NotNull String name) {
            myName = name;
        }

        @NotNull
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
        <T> void onChanged(@NotNull JiraIssueUiProperties.JiraIssueUiProperty<T> property);
    }

}
