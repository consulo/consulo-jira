package com.intellij.jira.ui.highlighters;

import com.intellij.jira.ui.JiraIssueUiProperties;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JiraIssueHighlighterProperty extends JiraIssueUiProperties.JiraIssueUiProperty<Boolean> {

    private static final Map<String, JiraIssueHighlighterProperty> ourProperties = new HashMap<>();
    private final String myId;

    public JiraIssueHighlighterProperty(@NotNull String name) {
        super("Highlighter." + name);
        this.myId = name;
    }

    @NotNull
    public String getId() {
        return myId;
    }

    @NotNull
    public static JiraIssueHighlighterProperty get(@NotNull String id) {
        JiraIssueHighlighterProperty property = ourProperties.get(id);
        if (Objects.isNull(property)) {
            property = new JiraIssueHighlighterProperty(id);
            ourProperties.put(id, property);
        }

        return property;
    }

}
