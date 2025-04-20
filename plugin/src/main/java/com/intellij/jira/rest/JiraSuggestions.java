package com.intellij.jira.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JiraSuggestions {

    private String token;
    private List<Suggestion> suggestions;

    public JiraSuggestions() {
        suggestions = new ArrayList<>();
    }

    public List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public List<String> getSuggestionLabels() {
        return getSuggestions().stream().map(Suggestion::getLabel).collect(Collectors.toList());
    }

    private class Suggestion {
        private String label;
        private String html;

        public Suggestion() {}

        public String getLabel() {
            return label;
        }
    }

}
