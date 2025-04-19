package com.intellij.jira.settings;

import com.intellij.jira.util.Separator;
import consulo.util.xml.serializer.annotation.OptionTag;

import java.util.Collections;
import java.util.List;

public class ChangelistState {

    private static final Separator DEFAULT_FIELD_SEPARATOR = Separator.HYPHEN;
    private static final List<String> DEFAULT_FIELD_NAMES = Collections.singletonList("issueKey");

    private Separator fieldSeparator;
    private List<String> fieldNames;
    private boolean active;
    private boolean custom;

    public ChangelistState() { }

    private ChangelistState(Separator fieldSeparator, List<String> fieldNames, boolean active, boolean custom) {
        this.fieldSeparator = fieldSeparator;
        this.fieldNames = fieldNames;
        this.active = active;
        this.custom = custom;
    }

    public static ChangelistState getDefault() {
        return new ChangelistState(DEFAULT_FIELD_SEPARATOR, DEFAULT_FIELD_NAMES, true, false);
    }

    public static ChangelistState getCustom(Separator fieldSeparator, List<String> fieldNames, boolean active) {
        return new ChangelistState(fieldSeparator, fieldNames, active, true);
    }

    @OptionTag("separator")
    public Separator getFieldSeparator() {
        return fieldSeparator;
    }

    @OptionTag
    public List<String> getFieldNames() {
        return fieldNames;
    }

    @OptionTag("active")
    public boolean isActive() {
        return active;
    }

    @OptionTag("custom")
    public boolean isCustom() {
        return custom;
    }

    public void setFieldSeparator(Separator fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }
}
