package com.intellij.jira.util;

import com.intellij.tasks.jira.CachedIconLoader;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class JiraIconUtil {
    private JiraIconUtil() { }

    public static consulo.ui.image.Image getIcon(@Nullable String iconUrl) {
        return CachedIconLoader.getIcon(iconUrl);
    }

}
