package com.intellij.jira.actions;

import consulo.ui.ex.action.CustomShortcutSet;
import consulo.ui.image.Image;
import jakarta.annotation.Nullable;

import static java.util.Objects.isNull;

public class ActionProperties {

    private String text;
    private String description;
    private Image icon;
    private CustomShortcutSet shortcut;

    public static ActionProperties of(String text){
        return new ActionProperties(text, null, null, null);
    }

    public static ActionProperties of(String text, Image icon){
        return new ActionProperties(text, null, icon, null);
    }

    public static ActionProperties of(String text, String description, Image icon){
        return new ActionProperties(text, description, icon, null);
    }

    public static ActionProperties of(String text, Image icon, String shortcut){
        return new ActionProperties(text, null, icon, shortcut);
    }

    private ActionProperties(@Nullable String text, @Nullable String description, @Nullable Image icon, @Nullable String shortcut) {
        this.text = text;
        this.description = description;
        this.icon = icon;
        this.shortcut = isNull(shortcut) ? CustomShortcutSet.EMPTY : CustomShortcutSet.fromString(shortcut);
    }


    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }

    public Image getIcon() {
        return icon;
    }

    public CustomShortcutSet getShortcut() {
        return shortcut;
    }
}
