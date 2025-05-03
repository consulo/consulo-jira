package com.intellij.jira.ui.editors;

import consulo.ui.ex.awt.ValidationInfo;
import jakarta.annotation.Nullable;

import javax.swing.*;

public interface Editor {

    JComponent createPanel();

    @Nullable
    ValidationInfo validate();

}
