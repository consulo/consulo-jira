package com.intellij.jira.util;

import com.intellij.tasks.jira.CachedIconLoader;
import jakarta.annotation.Nullable;

@Deprecated
public class JiraIconUtil {
    private JiraIconUtil() { }

    public static consulo.ui.image.Image getIcon(@Nullable String iconUrl) {
        return CachedIconLoader.getIcon(iconUrl);
    }

}
