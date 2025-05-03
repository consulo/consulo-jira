package com.intellij.jira.ui.highlighters;

import com.intellij.jira.ui.JiraIssueUiProperties;
import jakarta.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JiraIssueHighlighterProperty extends JiraIssueUiProperties.JiraIssueUiProperty<Boolean> {

    private static final Map<String, JiraIssueHighlighterProperty> ourProperties = new HashMap<>();
    private final String myId;

    public JiraIssueHighlighterProperty(@Nonnull String name) {
        super("Highlighter." + name);
        this.myId = name;
    }

    @Nonnull
    public String getId() {
        return myId;
    }

    @Nonnull
    public static JiraIssueHighlighterProperty get(@Nonnull String id) {
        JiraIssueHighlighterProperty property = ourProperties.get(id);
        if (Objects.isNull(property)) {
            property = new JiraIssueHighlighterProperty(id);
            ourProperties.put(id, property);
        }

        return property;
    }

}
