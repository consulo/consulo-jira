package com.intellij.jira.util;

import consulo.ui.ex.awt.JBUI;

import javax.swing.border.Border;

public final class JiraBorders {

    private JiraBorders() { }

    public static Border empty() {
        return empty(0, 0);
    }

    public static Border empty(int offsets) {
        return empty(offsets, offsets);
    }

    public static Border emptyTop(int offset) {
        return empty(offset, 0, 0, 0);
    }

    public static Border emptyRight(int offset) {
        return empty(0, 0, 0, offset);
    }

    public static Border emptyBottom(int offset) {
        return empty(0, 0, offset, 0);
    }

    public static Border emptyLeft(int offset) {
        return empty(0, offset, 0, 0);
    }

    public static Border empty(int topAndBottom, int leftAndRight) {
        return empty(topAndBottom, leftAndRight, topAndBottom, leftAndRight);
    }

    public static Border empty(int top, int left, int bottom, int right) {
        return JBUI.Borders.empty(top, left, bottom, right);
    }

}
