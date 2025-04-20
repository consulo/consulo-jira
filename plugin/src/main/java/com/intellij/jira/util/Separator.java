package com.intellij.jira.util;

public enum Separator {
    HYPHEN("Hypen", "-"),
    UNDERSCORE("Underscore", "_"),
    TILDE("Tilde", "~"),
    SLASH("Slash", "/"),
    PIPE("Pipe", "|");

    private String name;
    private String separator;

    Separator(String name, String separator) {
        this.name = name;
        this.separator = separator;
    }

    public String getName() {
        return name;
    }

    public String getSeparator() {
        return separator;
    }

    @Override
    public String toString() {
        return getName();
    }
}
