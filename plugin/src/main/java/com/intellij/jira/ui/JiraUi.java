package com.intellij.jira.ui;

import consulo.disposer.Disposable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Represents a Tab in Jira Window
 */
public interface JiraUi extends Disposable {

    @NotNull
    String getId();

    @NotNull
    JComponent getMainComponent();

}
