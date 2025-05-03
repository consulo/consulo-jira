package com.intellij.jira.ui;

import consulo.disposer.Disposable;
import jakarta.annotation.Nonnull;

import javax.swing.*;

/**
 * Represents a Tab in Jira Window
 */
public interface JiraUi extends Disposable {

    @Nonnull
    String getId();

    @Nonnull
    JComponent getMainComponent();

}
